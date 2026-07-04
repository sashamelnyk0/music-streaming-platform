package com.music.user.dto;

import lombok.*;

@Data
@Builder
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
}