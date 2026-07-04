package com.music.user.dto;

import com.music.user.model.SubscriptionType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private SubscriptionType subscription;
    private LocalDateTime createdAt;
}