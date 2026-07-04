package com.music.catalog.mapper;

import com.music.catalog.dto.genre.GenreRequestDto;
import com.music.catalog.dto.genre.GenreResponseDto;
import com.music.catalog.model.GenreEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GenreMapper {
    GenreResponseDto toDto(GenreEntity genre);
    GenreEntity toEntity(GenreRequestDto dto);
    List<GenreResponseDto> toDtoList(List<GenreEntity> genres);
}