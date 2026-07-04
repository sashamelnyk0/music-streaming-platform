package com.music.user.service;

import com.music.user.dto.*;
import com.music.user.exception.EmailAlreadyExistsException;
import com.music.user.exception.InvalidCredentialsException;
import com.music.user.exception.NotFoundException;
import com.music.user.mapper.UserMapper;
import com.music.user.model.RefreshTokenEntity;
import com.music.user.model.UserEntity;
import com.music.user.repository.RefreshTokenRepository;
import com.music.user.repository.UserRepository;
import com.music.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenEntity saveRefreshToken(String token, UserEntity user) {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .token(token)
                .userEntity(user)
                .expireAt(LocalDateTime.now().plusDays(30))
                .build();
        return refreshTokenRepository.save(entity);
    }

    @Transactional
    public AuthResponseDto register(RegisterRequestDto registerRequest){
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new EmailAlreadyExistsException(registerRequest.getEmail());
        }

        UserEntity saveUser = userMapper.toEntity(registerRequest);
        saveUser.setHashedPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(saveUser);

        String accessToken = jwtService.generateAccessToken(saveUser);
        String refreshToken = jwtService.generateRefreshToken(saveUser);
        saveRefreshToken(refreshToken, saveUser);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequest){
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + loginRequest.getEmail()));
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getHashedPassword())){
            throw new InvalidCredentialsException("Password doesn't match");
        }
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveRefreshToken(refreshToken, user);
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponseDto refreshToken(RefreshTokenRequestDto refreshToken) {
        RefreshTokenEntity refreshTokenEntity =  refreshTokenRepository.findByToken(refreshToken.getRefreshToken())
                .orElseThrow(() -> new NotFoundException("Refresh token not found: " + refreshToken));
        if(!refreshTokenEntity.isValid()){
            refreshTokenRepository.deleteByToken(refreshToken.getRefreshToken());
            throw new IllegalArgumentException("Token is expired");
        }
        UserEntity userEntity = refreshTokenEntity.getUserEntity();
        String accessToken = jwtService.generateAccessToken(userEntity);
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    @Transactional
    public void logout(RefreshTokenRequestDto refreshToken) {
        refreshTokenRepository.findByToken(refreshToken.getRefreshToken())
                        .ifPresent(refreshTokenRepository::delete);

    }

    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponseDto updateProfile(String email, UpdateProfileDto request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setName(request.getName());
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordDto request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if(!passwordEncoder.matches(request.getOldPassword(), user.getHashedPassword())){
            throw new InvalidCredentialsException("Password doesn't match");
        }
        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public UserResponseDto updateSubscription(String email, UpdateSubscriptionDto updateSubscriptionDto) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setSubscription(updateSubscriptionDto.getSubscription());
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
