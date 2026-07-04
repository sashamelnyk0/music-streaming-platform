package com.music.catalog.dto.track;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TrackResponseDto {
    private Long id;
    private String title;
    private int duration;
    private String fileUrl;
    private String imageUrl;
    private LocalDateTime createdAt;
    private Long albumId;
    private String albumTitle;
    private Long genreId;
    private String genreName;
}
