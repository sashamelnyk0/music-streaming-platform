package com.music.catalog.controller;

import com.music.catalog.dto.playlist.PlaylistRequestDto;
import com.music.catalog.dto.playlist.PlaylistResponseDto;
import com.music.catalog.security.SecurityUtils;
import com.music.catalog.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;

    @GetMapping("/me")
    public ResponseEntity<List<PlaylistResponseDto>> getMyPlaylists() {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(playlistService.getMyPlaylists(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponseDto> getPlaylistById(@PathVariable Long id) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(playlistService.getPlaylistById(id, email));
    }

    @PostMapping
    public ResponseEntity<PlaylistResponseDto> createPlaylist(
            @RequestBody @Valid PlaylistRequestDto request) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playlistService.createPlaylist(request, email));
    }

    @PostMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<PlaylistResponseDto> addTrackToPlaylist(
            @PathVariable Long id,
            @PathVariable Long trackId) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(playlistService.addTrackToPlaylist(id, trackId, email));
    }

    @DeleteMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<Void> removeTrackFromPlaylist(
            @PathVariable Long id,
            @PathVariable Long trackId) {
        String email = SecurityUtils.getCurrentUserEmail();
        playlistService.removeTrackFromPlaylist(id, trackId, email);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        String email = SecurityUtils.getCurrentUserEmail();
        playlistService.deletePlaylist(id, email);
        return ResponseEntity.noContent().build();
    }
}