package com.music.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProfileDto {
    private String name;
}
