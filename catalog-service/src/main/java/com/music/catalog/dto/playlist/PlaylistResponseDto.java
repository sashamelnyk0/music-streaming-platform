package com.music.catalog.dto.playlist;

import com.music.catalog.dto.track.TrackResponseDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PlaylistResponseDto {
    private Long id;
    private String name;
    private Long userId;
    private LocalDateTime createdAt;
    List<TrackResponseDto> tracks;
}
