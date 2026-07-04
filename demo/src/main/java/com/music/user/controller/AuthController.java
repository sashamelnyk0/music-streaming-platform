package com.music.user.controller;


import com.music.user.dto.AuthResponseDto;
import com.music.user.dto.LoginRequestDto;
import com.music.user.dto.RefreshTokenRequestDto;
import com.music.user.dto.RegisterRequestDto;
import com.music.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
           @RequestBody @Valid RegisterRequestDto registerRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(registerRequest));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @RequestBody @Valid LoginRequestDto loginRequest
    ){
        return ResponseEntity.ok(userService.login(loginRequest));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(
            @RequestBody @Valid RefreshTokenRequestDto refreshTokenRequestDto
    ){
        return ResponseEntity.ok(userService.refreshToken(refreshTokenRequestDto));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody @Valid RefreshTokenRequestDto refreshTokenRequestDto
    ){
        userService.logout(refreshTokenRequestDto);
        return ResponseEntity.noContent().build();
    }
}
