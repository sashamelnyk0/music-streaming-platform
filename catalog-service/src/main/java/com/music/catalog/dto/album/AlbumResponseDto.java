package com.music.catalog.dto.album;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AlbumResponseDto {
    private Long id;
    private String title;
    private String imageUrl;
    private LocalDate releaseDate;
    private LocalDateTime createdAt;
    private Long artistId;
    private String artistName;
}
