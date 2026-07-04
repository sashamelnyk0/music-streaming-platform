package com.music.analytics.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrackPlayedEventDto {
    private Long trackId;
    private String trackTitle;
    private String userEmail;
    private LocalDateTime listenedAt;
}
