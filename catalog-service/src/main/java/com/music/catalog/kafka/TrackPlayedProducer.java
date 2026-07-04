package com.music.catalog.kafka;

import com.music.catalog.dto.track.TrackPlayedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackPlayedProducer {

    private final KafkaTemplate<String, TrackPlayedEventDto> kafkaTemplate;

    public void publish(TrackPlayedEventDto event) {
        kafkaTemplate.send("track.played", event);
        log.info("Published event: trackId={}, user={}",
                event.getTrackId(),
                event.getUserEmail());
    }
}