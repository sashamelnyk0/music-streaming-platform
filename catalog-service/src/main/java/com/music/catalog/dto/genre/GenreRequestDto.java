package com.music.catalog.dto.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreRequestDto {
    @NotBlank
    private String name;
}
