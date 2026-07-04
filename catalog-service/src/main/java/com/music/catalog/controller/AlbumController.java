package com.music.catalog.controller;

import com.music.catalog.dto.album.AlbumRequestDto;
import com.music.catalog.dto.album.AlbumResponseDto;
import com.music.catalog.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping
    public ResponseEntity<List<AlbumResponseDto>> getAllAlbums(){
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getAlbumById(@PathVariable Long Id){
        return ResponseEntity.ok(albumService.getAlbumById(Id));
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<List<AlbumResponseDto>> getAlbumsByArtist(@PathVariable Long artistId){
        return ResponseEntity.ok(albumService.getAlbumsByArtist(artistId));
    }

    @PostMapping
    public ResponseEntity<AlbumResponseDto> createAlbum(@RequestBody @Valid AlbumRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.createAlbum(request));
    }
}
