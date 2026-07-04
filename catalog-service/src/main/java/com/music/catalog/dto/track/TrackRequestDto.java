package com.music.catalog.dto.track;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackRequestDto {
    @NotBlank
    private String title;
    @NotNull
    private int duration;
    @NotBlank
    private String fileUrl;
    private String imageUrl;
    @NotNull
    private Long albumId;
    @NotNull
    private Long genreId;
}
