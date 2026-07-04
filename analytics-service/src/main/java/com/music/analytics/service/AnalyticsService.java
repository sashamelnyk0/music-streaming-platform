package com.music.analytics.service;

import com.music.analytics.dto.TopTrackDto;
import com.music.analytics.model.TrackPlayedEvent;
import com.music.analytics.repository.TrackPlayedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TrackPlayedEventRepository repository;

    @Transactional(readOnly = true)
    public List<TrackPlayedEvent> getUserHistory(String userEmail) {
        return repository.findByUserEmailOrderByListenedAtDesc(userEmail);
    }

    @Transactional(readOnly = true)
    public List<TopTrackDto> getTopTracks() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return repository.findTopTracks(oneWeekAgo);
    }
}
