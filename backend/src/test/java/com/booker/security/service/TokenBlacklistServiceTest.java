package com.booker.security.service;

import com.booker.modules.log.service.LoggerService;
import com.booker.security.entity.InvalidatedToken;
import com.booker.security.repository.InvalidatedTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Mock
    private LoggerService loggerService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void invalidateToken_whenValidJwt_shouldSaveToRepository() {
        Jwt jwt = createMockJwt();
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(invalidatedTokenRepository.existsByToken(jwt.getTokenValue())).thenReturn(false);

        tokenBlacklistService.invalidateToken(authentication);

        verify(invalidatedTokenRepository).save(any(InvalidatedToken.class));
        verify(loggerService).success("Token invalidated successfully", "TokenBlacklistService");
    }

    @Test
    void invalidateToken_whenTokenAlreadyInvalidated_shouldNotSaveAgain() {
        Jwt jwt = createMockJwt();
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(invalidatedTokenRepository.existsByToken(jwt.getTokenValue())).thenReturn(true);

        tokenBlacklistService.invalidateToken(authentication);

        verify(invalidatedTokenRepository, never()).save(any());
        verify(loggerService, never()).success(anyString(), anyString());
    }

    @Test
    void invalidateToken_whenAuthenticationIsNull_shouldDoNothing() {
        tokenBlacklistService.invalidateToken(null);

        verify(invalidatedTokenRepository, never()).existsByToken(anyString());
        verify(invalidatedTokenRepository, never()).save(any());
    }

    @Test
    void invalidateToken_whenPrincipalIsNotJwt_shouldDoNothing() {
        when(authentication.getPrincipal()).thenReturn("not-a-jwt");

        tokenBlacklistService.invalidateToken(authentication);

        verify(invalidatedTokenRepository, never()).existsByToken(anyString());
        verify(invalidatedTokenRepository, never()).save(any());
    }

    @Test
    void isTokenInvalidated_whenTokenExists_shouldReturnTrue() {
        String token = "test-token";
        when(invalidatedTokenRepository.existsByToken(token)).thenReturn(true);

        boolean result = tokenBlacklistService.isTokenInvalidated(token);

        assertTrue(result);
        verify(invalidatedTokenRepository).existsByToken(token);
    }

    @Test
    void isTokenInvalidated_whenTokenDoesNotExist_shouldReturnFalse() {
        String token = "test-token";
        when(invalidatedTokenRepository.existsByToken(token)).thenReturn(false);

        boolean result = tokenBlacklistService.isTokenInvalidated(token);

        assertFalse(result);
        verify(invalidatedTokenRepository).existsByToken(token);
    }

    @Test
    void cleanupExpiredTokens_shouldDeleteExpiredTokens() {
        tokenBlacklistService.cleanupExpiredTokens();

        verify(invalidatedTokenRepository).deleteByExpiresAtBefore(any(Instant.class));
        verify(loggerService).info(anyString(), eq("TokenBlacklistService"));
    }

    private Jwt createMockJwt() {
        return Jwt.withTokenValue("test-token")
                .header("alg", "HS256")
                .claim("sub", "user-id")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();
    }
}
