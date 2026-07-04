package com.music.stream.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StreamUrlResponseDto {
    private String url;
    private String expiresIn;
}
