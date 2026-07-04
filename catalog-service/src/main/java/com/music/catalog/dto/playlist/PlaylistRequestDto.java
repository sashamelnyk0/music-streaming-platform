package com.music.catalog.dto.playlist;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistRequestDto {
    @NotBlank
    private String name;
}
