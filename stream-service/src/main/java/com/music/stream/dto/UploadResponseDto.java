package com.music.stream.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadResponseDto {
    private String fileKey;
}