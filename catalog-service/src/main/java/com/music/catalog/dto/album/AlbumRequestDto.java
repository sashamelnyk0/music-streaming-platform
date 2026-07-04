package com.music.catalog.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AlbumRequestDto {
    @NotBlank
    private String title;
    private String imageUrl;
    @NotNull
    private LocalDate releaseDate ;
    @NotNull
    private Long artistId;
}
