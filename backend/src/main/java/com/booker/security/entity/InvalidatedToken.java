package com.booker.security.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Represents an invalidated JWT token (blacklist).
 * Used to track tokens that have been logged out before their expiration.
 */
@Entity
@Table(name = "invalidated_tokens", indexes = @Index(name = "idx_token", columnList = "token"))
public class InvalidatedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1024)
    private String token;

    @Column(nullable = false)
    private Instant invalidatedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @PrePersist
    void onCreate() {
        this.invalidatedAt = Instant.now();
    }

    // Constructors
    public InvalidatedToken() {
    }

    public InvalidatedToken(String token, Instant expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    // Getters/Setters
    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getInvalidatedAt() {
        return invalidatedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
