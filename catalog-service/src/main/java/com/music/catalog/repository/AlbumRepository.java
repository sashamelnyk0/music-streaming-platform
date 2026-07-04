package com.music.catalog.repository;

import com.music.catalog.model.AlbumEntity;
import com.music.catalog.model.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, Long> {
    List<AlbumEntity> findByArtistEntity(ArtistEntity artist);
    List<AlbumEntity> findByTitleContainingIgnoreCase(String title);
}
