package com.booker.security.filter;

import com.booker.security.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistFilterTest {

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TokenBlacklistFilter tokenBlacklistFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void doFilterInternal_whenNoAuthentication_shouldContinueFilterChain() throws ServletException, IOException {
        when(securityContext.getAuthentication()).thenReturn(null);

        tokenBlacklistFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenBlacklistService);
    }

    @Test
    void doFilterInternal_whenTokenNotInBlacklist_shouldContinueFilterChain() throws ServletException, IOException {
        Jwt jwt = createMockJwt();
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(tokenBlacklistService.isTokenInvalidated(jwt.getTokenValue())).thenReturn(false);

        tokenBlacklistFilter.doFilterInternal(request, response, filterChain);

        verify(tokenBlacklistService).isTokenInvalidated(jwt.getTokenValue());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenTokenIsBlacklisted_shouldReturn401() throws ServletException, IOException {
        Jwt jwt = createMockJwt();
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(tokenBlacklistService.isTokenInvalidated(jwt.getTokenValue())).thenReturn(true);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        tokenBlacklistFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(filterChain, never()).doFilter(any(), any());
    }

    private Jwt createMockJwt() {
        return Jwt.withTokenValue("test-token")
                .header("alg", "HS256")
                .claim("sub", "user-id")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
