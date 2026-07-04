package com.music.catalog.service;

import com.music.catalog.dto.album.AlbumRequestDto;
import com.music.catalog.dto.album.AlbumResponseDto;
import com.music.catalog.exception.NotFoundException;
import com.music.catalog.mapper.AlbumMapper;
import com.music.catalog.model.AlbumEntity;
import com.music.catalog.model.ArtistEntity;
import com.music.catalog.repository.AlbumRepository;
import com.music.catalog.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final ArtistRepository artistRepository;

    @Transactional(readOnly = true)
    public List<AlbumResponseDto> getAllAlbums(){
        return albumMapper.toDtoList(albumRepository.findAll());
    }

    @Transactional(readOnly = true)
    public AlbumResponseDto getAlbumById(Long id){
        AlbumEntity albumEntity = albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Album not found with id: " + id));
        return albumMapper.toDto(albumEntity);
    }

    @Transactional(readOnly = true)
    public List<AlbumResponseDto> getAlbumsByArtist(Long artistId){
        ArtistEntity artistEntity = artistRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException("Artist not found with id: " + artistId));
        return albumMapper.toDtoList(albumRepository.findByArtistEntity(artistEntity));
    }

    @Transactional
    public AlbumResponseDto createAlbum(AlbumRequestDto albumRequestDto){
        ArtistEntity artistEntity = artistRepository.findById(albumRequestDto.getArtistId())
                .orElseThrow(() -> new NotFoundException("Artist not found with id: " + albumRequestDto.getArtistId()));
        AlbumEntity albumEntity = albumMapper.toEntity(albumRequestDto);
        albumEntity.setArtistEntity(artistEntity);
        albumRepository.save(albumEntity);
        return albumMapper.toDto(albumEntity);
    }
}
