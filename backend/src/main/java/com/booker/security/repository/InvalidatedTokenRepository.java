package com.booker.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.security.entity.InvalidatedToken;

import java.time.Instant;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, Long> {
    boolean existsByToken(String token);
    void deleteByExpiresAtBefore(Instant now);
}
