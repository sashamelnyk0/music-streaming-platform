package com.music.catalog.service;

import com.music.catalog.dto.playlist.PlaylistRequestDto;
import com.music.catalog.dto.playlist.PlaylistResponseDto;
import com.music.catalog.exception.AlreadyExistsException;
import com.music.catalog.exception.ForbiddenException;
import com.music.catalog.exception.NotFoundException;
import com.music.catalog.mapper.PlaylistMapper;
import com.music.catalog.model.PlaylistEntity;
import com.music.catalog.model.TrackEntity;
import com.music.catalog.repository.PlaylistRepository;
import com.music.catalog.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistMapper playlistMapper;
    private final TrackRepository trackRepository;

    @Transactional(readOnly = true)
    public List<PlaylistResponseDto> getMyPlaylists(String email) {
        return playlistMapper.toDtoList(playlistRepository.findByUserEmail(email));
    }

    @Transactional(readOnly = true)
    public PlaylistResponseDto getPlaylistById(Long id, String email) {
        PlaylistEntity playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Playlist not found with id: " + id));
        checkOwnership(playlist, email);
        return playlistMapper.toDto(playlist);
    }

    @Transactional
    public PlaylistResponseDto createPlaylist(PlaylistRequestDto request, String email) {
        if (playlistRepository.existsByNameAndUserEmail(request.getName(), email)) {
            throw new AlreadyExistsException("Playlist with this name already exists");
        }
        PlaylistEntity playlist = playlistMapper.toEntity(request);
        playlist.setUserEmail(email);
        playlistRepository.save(playlist);
        return playlistMapper.toDto(playlist);
    }

    @Transactional
    public PlaylistResponseDto addTrackToPlaylist(Long playlistId, Long trackId, String email) {
        PlaylistEntity playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found with id: " + playlistId));
        checkOwnership(playlist, email);

        TrackEntity track = trackRepository.findById(trackId)
                .orElseThrow(() -> new NotFoundException("Track not found with id: " + trackId));

        if (playlist.getTracks().contains(track)) {
            throw new AlreadyExistsException("Track already in playlist");
        }

        playlist.getTracks().add(track);
        playlistRepository.save(playlist);
        return playlistMapper.toDto(playlist);
    }

    @Transactional
    public void removeTrackFromPlaylist(Long playlistId, Long trackId, String email) {
        PlaylistEntity playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found with id: " + playlistId));
        checkOwnership(playlist, email);

        TrackEntity track = trackRepository.findById(trackId)
                .orElseThrow(() -> new NotFoundException("Track not found with id: " + trackId));

        playlist.getTracks().remove(track);
        playlistRepository.save(playlist);
    }

    @Transactional
    public void deletePlaylist(Long id, String email) {
        PlaylistEntity playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Playlist not found with id: " + id));
        checkOwnership(playlist, email);
        playlistRepository.delete(playlist);
    }

    private void checkOwnership(PlaylistEntity playlist, String email) {
        if (!playlist.getUserEmail().equals(email)) {
            throw new ForbiddenException("You don't have access to this playlist");
        }
    }
}
