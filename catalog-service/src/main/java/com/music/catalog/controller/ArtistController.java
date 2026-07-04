package com.music.catalog.controller;


import com.music.catalog.dto.artist.ArtistRequestDto;
import com.music.catalog.dto.artist.ArtistResponseDto;
import com.music.catalog.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;

    @GetMapping
    public ResponseEntity<List<ArtistResponseDto>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponseDto> getArtistById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArtistResponseDto>> searchArtists(@RequestParam String name) {
        return ResponseEntity.ok(artistService.searchArtists(name));
    }

    @PostMapping
    public ResponseEntity<ArtistResponseDto> createArtist(@RequestBody @Valid ArtistRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.createArtist(request));
    }
}