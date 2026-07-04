package com.music.catalog.mapper;

import com.music.catalog.dto.artist.ArtistRequestDto;
import com.music.catalog.dto.artist.ArtistResponseDto;
import com.music.catalog.model.ArtistEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArtistMapper {
    ArtistResponseDto toDto(ArtistEntity genre);
    ArtistEntity toEntity(ArtistRequestDto dto);
    List<ArtistResponseDto> toDtoList(List<ArtistEntity> genres);
}
