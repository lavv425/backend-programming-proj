package com.booker.modules.customer.dto;

import java.time.Instant;
import java.util.UUID;

public class CustomerPublicDto {
    public UUID id;
    public String email;
    public String firstName;
    public String lastName;
    public UUID role;
    public String profileImageUrl;
    public Instant createdAt;

    public String phoneNumber;
    public Integer loyaltyPoints;

    public CustomerPublicDto(UUID id, String email, String firstName, String lastName, UUID role, String profileImageUrl, Instant createdAt, String phoneNumber, Integer loyaltyPoints) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.phoneNumber = phoneNumber;
        this.loyaltyPoints = loyaltyPoints;
    }
}
