package com.music.analytics.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "track_played_events")
public class TrackPlayedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long trackId;

    @Column(nullable = false)
    private String trackTitle;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private LocalDateTime listenedAt;
}
