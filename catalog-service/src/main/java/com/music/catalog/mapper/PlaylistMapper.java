package com.music.catalog.mapper;

import com.music.catalog.dto.playlist.PlaylistRequestDto;
import com.music.catalog.dto.playlist.PlaylistResponseDto;
import com.music.catalog.model.PlaylistEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {TrackMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlaylistMapper {
    @Mapping(target = "tracks", source = "tracks")
    PlaylistResponseDto toDto(PlaylistEntity playlist);

    List<PlaylistResponseDto> toDtoList(List<PlaylistEntity> playlistEntities);

    @Mapping(target = "userEmail", ignore = true)
    @Mapping(target = "tracks", ignore = true)
    PlaylistEntity toEntity(PlaylistRequestDto dto);
}
