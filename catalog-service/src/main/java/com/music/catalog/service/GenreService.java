package com.music.catalog.service;

import com.music.catalog.dto.genre.GenreRequestDto;
import com.music.catalog.dto.genre.GenreResponseDto;
import com.music.catalog.exception.AlreadyExistsException;
import com.music.catalog.exception.NotFoundException;
import com.music.catalog.mapper.GenreMapper;
import com.music.catalog.model.GenreEntity;
import com.music.catalog.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Transactional(readOnly = true)
    public List<GenreResponseDto> getAllGenres(){
        return genreMapper.toDtoList(genreRepository.findAll());
    }

    @Transactional(readOnly = true)
    public GenreResponseDto getGenreById(Long id){
        GenreEntity genre = genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Genre not found"));
        return genreMapper.toDto(genre);
    }

    @Transactional
    public GenreResponseDto createGenre(GenreRequestDto genreRequestDto){
        if(genreRepository.existsByName(genreRequestDto.getName())){
            throw new AlreadyExistsException("Name already exists");
        }
        GenreEntity saveGenre = genreMapper.toEntity(genreRequestDto);
        genreRepository.save(saveGenre);
        return genreMapper.toDto(saveGenre);
    }
}
