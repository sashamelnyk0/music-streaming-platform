package com.music.catalog.repository;

import com.music.catalog.model.PlaylistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long> {
    List<PlaylistEntity> findByUserEmail(String email);
    boolean existsByNameAndUserEmail(String name, String email);
}
