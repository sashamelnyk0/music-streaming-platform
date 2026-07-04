package com.music.catalog.dto.artist;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ArtistResponseDto {
    private Long id;
    private String name;
    private String bio;
    private String imageUrl;
    private LocalDateTime createdAt;
}