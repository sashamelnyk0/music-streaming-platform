package com.music.catalog.dto.track;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TrackPlayedEventDto {
    private Long trackId;
    private String trackTitle;
    private String userEmail;
    private LocalDateTime listenedAt;
}