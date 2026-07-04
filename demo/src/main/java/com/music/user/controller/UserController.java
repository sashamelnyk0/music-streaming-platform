package com.music.user.controller;


import com.music.user.dto.ChangePasswordDto;
import com.music.user.dto.UpdateProfileDto;
import com.music.user.dto.UpdateSubscriptionDto;
import com.music.user.dto.UserResponseDto;
import com.music.user.security.SecurityUtils;
import com.music.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(){
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(userService.getCurrentUser(email));
    }
    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateProfile(
            @RequestBody @Valid UpdateProfileDto request
    ){
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(userService.updateProfile(email, request));
    }
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordDto request
    ){
        String email = SecurityUtils.getCurrentUserEmail();
        userService.changePassword(email, request);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/me/subscription")
    public ResponseEntity<UserResponseDto> updateSubscription(
            @RequestBody @Valid UpdateSubscriptionDto updateSubscriptionDto
            )
    {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(userService.updateSubscription(email, updateSubscriptionDto));
    }
}
