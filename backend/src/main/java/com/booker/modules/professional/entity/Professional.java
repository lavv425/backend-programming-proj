package com.booker.modules.professional.entity;

import jakarta.persistence.*;

import com.booker.modules.user.entity.User;

/**
 * Represents a professional user who offers services to customers.
 * Extends the base User class with professional-specific information like bio,
 * years of experience, verification status, and average rating.
 */
@Entity
@Table(name = "professionals", uniqueConstraints = @UniqueConstraint(name = "uk_professionals_phone", columnNames = "phone_number"), indexes = @Index(name = "idx_professionals_phone", columnList = "phone_number"))
public class Professional extends User {

    @Column(length = 1000, nullable = false)
    private String bio;
    
    @Column(name = "experience", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer yearsOfExperience;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerified;

    @Column(columnDefinition = "INT DEFAULT 0", nullable = false)
    private Integer averageRating;

    // getters/setters

    public String getBio() {
        return bio;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public Integer getAverageRating() {
        return averageRating;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public void setAverageRating(Integer averageRating) {
        this.averageRating = averageRating;
    }
}
