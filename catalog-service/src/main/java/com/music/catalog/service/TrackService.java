package com.music.catalog.service;

import com.music.catalog.dto.track.TrackPlayedEventDto;
import com.music.catalog.dto.track.TrackRequestDto;
import com.music.catalog.dto.track.TrackResponseDto;
import com.music.catalog.exception.NotFoundException;
import com.music.catalog.kafka.TrackPlayedProducer;
import com.music.catalog.mapper.TrackMapper;
import com.music.catalog.model.AlbumEntity;
import com.music.catalog.model.GenreEntity;
import com.music.catalog.model.TrackEntity;
import com.music.catalog.repository.AlbumRepository;
import com.music.catalog.repository.GenreRepository;
import com.music.catalog.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {
    private final TrackMapper trackMapper;
    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final TrackPlayedProducer producer;

    @Transactional(readOnly = true)
    public List<TrackResponseDto> getAllTracks(){
        return trackMapper.toDtoList(trackRepository.findAll());
    }

    @Transactional(readOnly = true)
    public TrackResponseDto getTrackById(Long id, String userEmail) {
        TrackEntity track = trackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Track not found with id: " + id));

        producer.publish(TrackPlayedEventDto.builder()
                .trackId(track.getId())
                .trackTitle(track.getTitle())
                .userEmail(userEmail)
                .listenedAt(LocalDateTime.now())
                .build());

        return trackMapper.toDto(track);
    }

    @Transactional(readOnly = true)
    public List<TrackResponseDto> getTracksByAlbum(Long albumId){
        AlbumEntity albumEntity = albumRepository.findById(albumId)
                        .orElseThrow(() -> new NotFoundException("Album not found with id: " + albumId));
        return trackMapper.toDtoList(trackRepository.findByAlbumEntity(albumEntity));
    }

    @Transactional(readOnly = true)
    public List<TrackResponseDto> searchTracks(String title){
        return trackMapper.toDtoList(trackRepository.findByTitleContainingIgnoreCase(title));
    }

    @Transactional
    public TrackResponseDto createTrack(TrackRequestDto trackRequestDto){
        AlbumEntity albumEntity = albumRepository.findById(trackRequestDto.getAlbumId())
                .orElseThrow(() -> new NotFoundException("Album not found with id: " + trackRequestDto.getAlbumId()));
        GenreEntity genreEntity = genreRepository.findById(trackRequestDto.getGenreId())
                .orElseThrow(() -> new NotFoundException("Genre not found with id: " + trackRequestDto.getGenreId()));

        TrackEntity saveTrack = trackMapper.toEntity(trackRequestDto);
        saveTrack.setAlbumEntity(albumEntity);
        saveTrack.setGenreEntity(genreEntity);
        trackRepository.save(saveTrack);
        return trackMapper.toDto(saveTrack);
    }
}
