package com.booker.modules.user.dto;

import java.time.Instant;
import java.util.UUID;

public class UserPublicDto {
    public UUID id;
    public String email;
    public String firstName;
    public String lastName;
    public UUID role;
    public String profileImageUrl;
    public Instant createdAt;

    public UserPublicDto(UUID id, String email, String firstName, String lastName, UUID role, String profileImageUrl, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
    }
}
