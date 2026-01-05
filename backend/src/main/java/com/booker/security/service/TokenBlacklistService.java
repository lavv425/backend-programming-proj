package com.booker.security.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booker.security.entity.InvalidatedToken;
import com.booker.security.repository.InvalidatedTokenRepository;
import com.booker.modules.log.service.LoggerService;

import java.time.Instant;

/**
 * Service for managing invalidated JWT tokens (blacklist).
 * Provides methods to invalidate tokens on logout and check if a token is blacklisted.
 */
@Service
public class TokenBlacklistService {

    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final LoggerService loggerService;

    public TokenBlacklistService(InvalidatedTokenRepository invalidatedTokenRepository, LoggerService loggerService) {
        this.invalidatedTokenRepository = invalidatedTokenRepository;
        this.loggerService = loggerService;
    }

    /**
     * Invalidates a token by adding it to the blacklist.
     *
     * @param authentication the authentication object containing the JWT
     */
    @Transactional
    public void invalidateToken(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String tokenValue = jwt.getTokenValue();
        Instant expiresAt = jwt.getExpiresAt();

        if (!invalidatedTokenRepository.existsByToken(tokenValue)) {
            InvalidatedToken invalidatedToken = new InvalidatedToken(tokenValue, expiresAt);
            invalidatedTokenRepository.save(invalidatedToken);
            loggerService.success("Token invalidated successfully", "TokenBlacklistService");
        }
    }

    /**
     * Checks if a token is in the blacklist.
     *
     * @param token the JWT token string
     * @return true if the token is invalidated, false otherwise
     */
    public boolean isTokenInvalidated(String token) {
        return invalidatedTokenRepository.existsByToken(token);
    }

    /**
     * Scheduled task to clean up expired tokens from the blacklist.
     * Runs daily at 3 AM.
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            invalidatedTokenRepository.deleteByExpiresAtBefore(Instant.now());
            loggerService.info("Cleaned up expired tokens from blacklist", "TokenBlacklistService");
        } catch (Exception e) {
            loggerService.error("Failed to cleanup expired tokens: " + e.getMessage(), "TokenBlacklistService");
        }
    }
}
