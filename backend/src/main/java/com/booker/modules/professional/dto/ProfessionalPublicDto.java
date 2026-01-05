package com.booker.modules.professional.dto;

import java.time.Instant;
import java.util.UUID;

public class ProfessionalPublicDto {
    public UUID id;
    public String email;
    public String firstName;
    public String lastName;
    public UUID role;
    public String profileImageUrl;
    public Instant createdAt;

    public String bio;
    public Integer yearsOfExperience;
    public Boolean isVerified;
    public Integer averageRating;

    public ProfessionalPublicDto(
            UUID id,
            String email,
            String firstName,
            String lastName,
            UUID role,
            String profileImageUrl,
            Instant createdAt,
            String bio,
            Integer yearsOfExperience,
            Boolean isVerified,
            Integer averageRating
    ) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.bio = bio;
        this.yearsOfExperience = yearsOfExperience;
        this.isVerified = isVerified;
        this.averageRating = averageRating;
    }
}
