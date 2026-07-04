package com.music.catalog.mapper;

import com.music.catalog.dto.track.TrackRequestDto;
import com.music.catalog.dto.track.TrackResponseDto;
import com.music.catalog.model.TrackEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrackMapper {
    @Mapping(target = "albumId", source = "albumEntity.id")
    @Mapping(target = "albumTitle", source = "albumEntity.title")
    @Mapping(target = "genreId", source = "genreEntity.id")
    @Mapping(target = "genreName", source = "genreEntity.name")
    TrackResponseDto toDto(TrackEntity track);

    @Mapping(target = "albumEntity", ignore = true)
    @Mapping(target = "genreEntity", ignore = true)
    TrackEntity toEntity(TrackRequestDto dto);

    List<TrackResponseDto> toDtoList(List<TrackEntity> tracks);
}
