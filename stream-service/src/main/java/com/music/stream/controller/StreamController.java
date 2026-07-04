package com.music.stream.controller;

import com.music.stream.dto.StreamUrlResponseDto;
import com.music.stream.dto.UploadResponseDto;
import com.music.stream.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/stream")
@RequiredArgsConstructor
public class StreamController {
    private final MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponseDto> updload(@RequestParam("file") MultipartFile file){
        String fileKey = minioService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UploadResponseDto.builder()
                        .fileKey(fileKey)
                        .build());
    }

    @GetMapping("/{fileKey}")
    public ResponseEntity<StreamUrlResponseDto> getStream(@PathVariable String fileKey){
        String stream = minioService.getPresignedUrl(fileKey);
        return ResponseEntity.status(HttpStatus.OK)
                .body(StreamUrlResponseDto.builder()
                        .url(stream)
                        .expiresIn("15 minutes")
                        .build());
    }

    @DeleteMapping("/{fileKey}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileKey){
        minioService.deleteFile(fileKey);
        return ResponseEntity.noContent().build();
    }
}
