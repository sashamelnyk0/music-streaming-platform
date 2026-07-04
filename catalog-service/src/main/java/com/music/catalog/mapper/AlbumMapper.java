package com.music.catalog.mapper;

import com.music.catalog.dto.album.AlbumRequestDto;
import com.music.catalog.dto.album.AlbumResponseDto;
import com.music.catalog.model.AlbumEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {
    @Mapping(target = "artistId", source = "artistEntity.id")
    @Mapping(target = "artistName", source = "artistEntity.name")
    AlbumResponseDto toDto(AlbumEntity album);

    @Mapping(target = "artistEntity", ignore = true)
    AlbumEntity toEntity(AlbumRequestDto dto);

    List<AlbumResponseDto> toDtoList(List<AlbumEntity> albums);
}
