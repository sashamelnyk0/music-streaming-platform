package com.music.analytics.repository;

import com.music.analytics.dto.TopTrackDto;
import com.music.analytics.model.TrackPlayedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrackPlayedEventRepository extends JpaRepository<TrackPlayedEvent, Long> {
    List<TrackPlayedEvent> findByUserEmailOrderByListenedAtDesc(String userEmail);

    @Query("""
        SELECT new com.music.analytics.dto.TopTrackDto(
            t.trackId,
            t.trackTitle,
            COUNT(t)
        )
        FROM TrackPlayedEvent t
        WHERE t.listenedAt >= :from
        GROUP BY t.trackId, t.trackTitle
        ORDER BY COUNT(t) DESC
        LIMIT 10
    """)
    List<TopTrackDto> findTopTracks(LocalDateTime from);
}
