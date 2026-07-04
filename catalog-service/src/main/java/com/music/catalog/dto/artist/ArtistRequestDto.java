package com.music.catalog.dto.artist;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArtistRequestDto {
    @NotBlank
    private String name;
    private String bio;
    private String imageUrl;
}
