package com.music.catalog.service;

import com.music.catalog.dto.artist.ArtistRequestDto;
import com.music.catalog.dto.artist.ArtistResponseDto;
import com.music.catalog.exception.AlreadyExistsException;
import com.music.catalog.exception.NotFoundException;
import com.music.catalog.mapper.ArtistMapper;
import com.music.catalog.model.ArtistEntity;
import com.music.catalog.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistMapper artistMapper;
    private final ArtistRepository artistRepository;

    @Transactional(readOnly = true)
    public List<ArtistResponseDto> getAllArtists(){
        return artistMapper.toDtoList(artistRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ArtistResponseDto getArtistById(Long id){
        ArtistEntity artistEntity = artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found with id: " + id));
        return artistMapper.toDto(artistEntity);
    }

    @Transactional(readOnly = true)
    public List<ArtistResponseDto> searchArtists(String name){
        return artistMapper.toDtoList(artistRepository.findByNameContainingIgnoreCase(name));
    }

    @Transactional
    public ArtistResponseDto createArtist(ArtistRequestDto artistRequestDto){
        if(artistRepository.existsByName(artistRequestDto.getName())){
            throw new AlreadyExistsException("Artist with this name already exists");
        }
        ArtistEntity saveArtist = artistMapper.toEntity(artistRequestDto);
        artistRepository.save(saveArtist);
        return artistMapper.toDto(saveArtist);
    }
}
