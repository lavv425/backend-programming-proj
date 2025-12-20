package com.booker.modules.professional.entity;

import jakarta.persistence.*;

import com.booker.modules.user.entity.User;

@Entity
@Table(name = "professionals", uniqueConstraints = @UniqueConstraint(name = "uk_professionals_phone", columnNames = "phone_number"), indexes = @Index(name = "idx_professionals_phone", columnList = "phone_number"))
public class Professional extends User {

    @Column(length = 1000, nullable = false)
    private String bio;
    
    @Column(name = "experience", nullable = false, columnDefinition = "INT DEFAULT 0")
    private String yearsOfExperience;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerified;

    @Column(columnDefinition = "INT DEFAULT 0", nullable = false)
    private Integer averageRating;

    // getters/setters

    public String getBio() {
        return bio;
    }

    public String getYearsOfExperience() {
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

    public void setYearsOfExperience(String yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public void setAverageRating(Integer averageRating) {
        this.averageRating = averageRating;
    }
}
