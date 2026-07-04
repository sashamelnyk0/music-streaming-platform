package com.music.analytics.controller;

import com.music.analytics.dto.TopTrackDto;
import com.music.analytics.model.TrackPlayedEvent;
import com.music.analytics.security.SecurityUtils;
import com.music.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/history")
    public ResponseEntity<List<TrackPlayedEvent>> getUserHistory() {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(analyticsService.getUserHistory(email));
    }

    @GetMapping("/top")
    public ResponseEntity<List<TopTrackDto>> getTopTracks() {
        return ResponseEntity.ok(analyticsService.getTopTracks());
    }
}
