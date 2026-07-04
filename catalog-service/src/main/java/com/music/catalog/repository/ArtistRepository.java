package com.music.catalog.repository;

import com.music.catalog.model.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
    Optional<ArtistEntity> findByName(String name);
    boolean existsByName(String name);
    List<ArtistEntity> findByNameContainingIgnoreCase(String name);
}
