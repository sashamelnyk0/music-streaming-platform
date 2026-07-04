package com.music.analytics.consumer;


import com.music.analytics.dto.TrackPlayedEventDto;
import com.music.analytics.model.TrackPlayedEvent;
import com.music.analytics.repository.TrackPlayedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackPlayedConsumer {

    private final TrackPlayedEventRepository repository;

    @KafkaListener(
            topics = "track.played",
            groupId = "analytics-group"
    )
    public void consume(TrackPlayedEventDto event) {
        log.info("Received event: trackId={}, user={}", event.getTrackId(), event.getUserEmail());

        TrackPlayedEvent entity = TrackPlayedEvent.builder()
                .trackId(event.getTrackId())
                .trackTitle(event.getTrackTitle())
                .userEmail(event.getUserEmail())
                .listenedAt(event.getListenedAt())
                .build();

        repository.save(entity);
    }
}