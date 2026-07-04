package com.music.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class LoginRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
