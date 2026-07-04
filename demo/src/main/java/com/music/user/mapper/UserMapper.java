package com.music.user.mapper;

import com.music.user.dto.RegisterRequestDto;
import com.music.user.dto.UserResponseDto;
import com.music.user.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserResponseDto toDto(UserEntity user);
    List<UserResponseDto> toDtoList(List<UserEntity> users);

    @Mapping(target = "hashedPassword", ignore = true)
    UserEntity toEntity(RegisterRequestDto registerRequestDto);
}
