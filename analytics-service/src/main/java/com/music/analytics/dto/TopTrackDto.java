package com.music.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopTrackDto {
    private Long trackId;
    private String trackTitle;
    private Long playCount;
}
