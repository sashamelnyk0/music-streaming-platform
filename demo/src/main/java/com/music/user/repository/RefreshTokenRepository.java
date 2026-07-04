package com.music.user.repository;

import com.music.user.model.RefreshTokenEntity;
import com.music.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByUserEntity(UserEntity user);
    void deleteByToken(String token);
}
