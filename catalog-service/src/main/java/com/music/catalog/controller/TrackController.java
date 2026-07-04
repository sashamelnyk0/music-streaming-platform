package com.music.catalog.controller;

import com.music.catalog.dto.track.TrackRequestDto;
import com.music.catalog.dto.track.TrackResponseDto;
import com.music.catalog.security.SecurityUtils;
import com.music.catalog.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/tracks")
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;

    @GetMapping
    public ResponseEntity<List<TrackResponseDto>> getAllTracks() {
        return ResponseEntity.ok(trackService.getAllTracks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackResponseDto> getTrackById(@PathVariable Long id) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(trackService.getTrackById(id, email));
    }

    @GetMapping("/album/{id}")
    public ResponseEntity<List<TrackResponseDto>> getTracksByAlbum(@PathVariable Long id) {
        return ResponseEntity.ok(trackService.getTracksByAlbum(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrackResponseDto>> searchTracks(@RequestParam String title) {
        return ResponseEntity.ok(trackService.searchTracks(title));
    }

    @PostMapping
    public ResponseEntity<TrackResponseDto> createTrack(@RequestBody @Valid TrackRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trackService.createTrack(request));
    }
}
