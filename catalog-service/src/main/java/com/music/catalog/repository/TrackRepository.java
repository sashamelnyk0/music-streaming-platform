package com.music.catalog.repository;

import com.music.catalog.model.AlbumEntity;
import com.music.catalog.model.GenreEntity;
import com.music.catalog.model.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, Long> {
    List<TrackEntity> findByAlbumEntity(AlbumEntity album);
    List<TrackEntity> findByGenreEntity(GenreEntity genre);
    List<TrackEntity> findByTitleContainingIgnoreCase(String title);
}
