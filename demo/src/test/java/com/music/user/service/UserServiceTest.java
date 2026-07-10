package com.music.user.service;

import com.music.user.dto.*;
import com.music.user.exception.EmailAlreadyExistsException;
import com.music.user.exception.InvalidCredentialsException;
import com.music.user.exception.NotFoundException;
import com.music.user.mapper.UserMapper;
import com.music.user.model.RefreshTokenEntity;
import com.music.user.model.SubscriptionType;
import com.music.user.model.UserEntity;
import com.music.user.repository.RefreshTokenRepository;
import com.music.user.repository.UserRepository;
import com.music.user.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;
    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(1L)
                .name("Test User")
                .email("test@gmail.com")
                .hashedPassword("hashedPassword123")
                .subscription(SubscriptionType.FREE)
                .createdAt(LocalDateTime.now())
                .build();

        registerRequest = new RegisterRequestDto();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@gmail.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@gmail.com");
        loginRequest.setPassword("password123");
    }

    @Nested
    @DisplayName("register()")
    class RegisterTests {

        @Test
        @DisplayName("Success")
        void register_success() {
            when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
            when(userMapper.toEntity(registerRequest)).thenReturn(testUser);
            when(passwordEncoder.encode("password123")).thenReturn("hashedPassword123");
            when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);
            when(jwtService.generateAccessToken(any())).thenReturn("access-token");
            when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");
            when(refreshTokenRepository.save(any())).thenReturn(new RefreshTokenEntity());

            AuthResponseDto result = userService.register(registerRequest);

            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo("access-token");
            assertThat(result.getRefreshToken()).isEqualTo("refresh-token");

            verify(userRepository).save(any(UserEntity.class));
            verify(refreshTokenRepository).save(any(RefreshTokenEntity.class));
            verify(passwordEncoder).encode("password123");
        }

        @Test
        @DisplayName("Email exists exception")
        void register_emailAlreadyExists_throwsException() {
            when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.register(registerRequest))
                    .isInstanceOf(EmailAlreadyExistsException.class);

            verify(userRepository, never()).save(any());
            verify(jwtService, never()).generateAccessToken(any());
        }

        @Test
        @DisplayName("Hashes password")
        void register_passwordIsHashed() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userMapper.toEntity(any())).thenReturn(testUser);
            when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedValue");
            when(userRepository.save(any())).thenReturn(testUser);
            when(jwtService.generateAccessToken(any())).thenReturn("token");
            when(jwtService.generateRefreshToken(any())).thenReturn("refresh");
            when(refreshTokenRepository.save(any())).thenReturn(new RefreshTokenEntity());

            userService.register(registerRequest);

            verify(passwordEncoder).encode("password123");
        }
    }

    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("Success")
        void login_success() {
            when(userRepository.findByEmail("test@gmail.com"))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "hashedPassword123"))
                    .thenReturn(true);
            when(jwtService.generateAccessToken(testUser)).thenReturn("access-token");
            when(jwtService.generateRefreshToken(testUser)).thenReturn("refresh-token");
            when(refreshTokenRepository.save(any())).thenReturn(new RefreshTokenEntity());

            AuthResponseDto result = userService.login(loginRequest);

            assertThat(result.getAccessToken()).isEqualTo("access-token");
            assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        }

        @Test
        @DisplayName("Not found exception")
        void login_userNotFound_throwsException() {
            when(userRepository.findByEmail("test@gmail.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.login(loginRequest))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("test@gmail.com");

            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("Invalid credentials exception")
        void login_wrongPassword_throwsException() {
            when(userRepository.findByEmail("test@gmail.com"))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "hashedPassword123"))
                    .thenReturn(false);

            assertThatThrownBy(() -> userService.login(loginRequest))
                    .isInstanceOf(InvalidCredentialsException.class);

            verify(jwtService, never()).generateAccessToken(any());
        }
    }

    @Nested
    @DisplayName("getCurrentUser()")
    class GetCurrentUserTests {

        @Test
        @DisplayName("Success")
        void getCurrentUser_success() {
            UserResponseDto expectedDto = UserResponseDto.builder()
                    .id(1L)
                    .name("Test User")
                    .email("test@gmail.com")
                    .subscription(SubscriptionType.FREE)
                    .build();

            when(userRepository.findByEmail("test@gmail.com"))
                    .thenReturn(Optional.of(testUser));
            when(userMapper.toDto(testUser)).thenReturn(expectedDto);

            UserResponseDto result = userService.getCurrentUser("test@gmail.com");

            assertThat(result.getEmail()).isEqualTo("test@gmail.com");
            assertThat(result.getName()).isEqualTo("Test User");
        }

        @Test
        @DisplayName("Not found exception")
        void getCurrentUser_notFound_throwsException() {
            when(userRepository.findByEmail("unknown@gmail.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getCurrentUser("unknown@gmail.com"))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("changePassword()")
    class ChangePasswordTests {

        @Test
        @DisplayName("Success")
        void changePassword_success() {
            ChangePasswordDto request = new ChangePasswordDto();
            request.setOldPassword("password123");
            request.setNewPassword("newPassword456");

            when(userRepository.findByEmail("test@gmail.com"))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "hashedPassword123"))
                    .thenReturn(true);
            when(passwordEncoder.encode("newPassword456"))
                    .thenReturn("newHashedPassword");

            userService.changePassword("test@gmail.com", request);

            verify(userRepository).save(testUser);
            assertThat(testUser.getHashedPassword()).isEqualTo("newHashedPassword");
        }

        @Test
        @DisplayName("Invalid credentials exception")
        void changePassword_wrongOldPassword_throwsException() {
            ChangePasswordDto request = new ChangePasswordDto();
            request.setOldPassword("wrongPassword");
            request.setNewPassword("newPassword456");

            when(userRepository.findByEmail("test@gmail.com"))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongPassword", "hashedPassword123"))
                    .thenReturn(false);

            assertThatThrownBy(() -> userService.changePassword("test@gmail.com", request))
                    .isInstanceOf(InvalidCredentialsException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateProfile()")
    class UpdateProfileTests {

        @Test
        @DisplayName("Success")
        void updateProfile_success() {
            UpdateProfileDto request = UpdateProfileDto.builder()
                    .name("New Name")
                    .build();

            UserResponseDto expectedDto = UserResponseDto.builder()
                    .name("New Name")
                    .email("test@gmail.com")
                    .build();

            when(userRepository.findByEmail("test@gmail.com"))
                    .thenReturn(Optional.of(testUser));
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(userMapper.toDto(testUser)).thenReturn(expectedDto);

            UserResponseDto result = userService.updateProfile("test@gmail.com", request);

            assertThat(result.getName()).isEqualTo("New Name");
            assertThat(testUser.getName()).isEqualTo("New Name");
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Not found exception")
        void updateProfile_userNotFound_throwsException() {
            UpdateProfileDto request = UpdateProfileDto.builder()
                    .name("New Name")
                    .build();

            when(userRepository.findByEmail("unknown@gmail.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateProfile("unknown@gmail.com", request))
                    .isInstanceOf(NotFoundException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("logout()")
    class LogoutTests {

        @Test
        @DisplayName("Success")
        void logout_success() {
            RefreshTokenRequestDto request = new RefreshTokenRequestDto();
            request.setRefreshToken("valid-refresh-token");

            RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                    .token("valid-refresh-token")
                    .userEntity(testUser)
                    .expireAt(LocalDateTime.now().plusDays(30))
                    .build();

            when(refreshTokenRepository.findByToken("valid-refresh-token"))
                    .thenReturn(Optional.of(tokenEntity));

            userService.logout(request);

            verify(refreshTokenRepository).delete(tokenEntity);
        }

        @Test
        @DisplayName("Ignores missing token")
        void logout_tokenNotFound_doesNothing() {
            RefreshTokenRequestDto request = new RefreshTokenRequestDto();
            request.setRefreshToken("non-existent-token");

            when(refreshTokenRepository.findByToken("non-existent-token"))
                    .thenReturn(Optional.empty());

            assertThatNoException().isThrownBy(() -> userService.logout(request));

            verify(refreshTokenRepository, never()).delete(any());
        }
    }
}