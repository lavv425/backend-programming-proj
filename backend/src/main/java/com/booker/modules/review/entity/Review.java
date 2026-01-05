package com.booker.modules.review.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a customer review for a professional after an appointment.
 * Reviews include a rating (typically 1-5 stars) and optional written feedback.
 * Each review is linked to a specific appointment.
 */
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 2000)
    private String comment;

    @Column(name = "customer_uuid", nullable = false)
    private UUID customer;

    @Column(name = "professional_uuid", nullable = false)
    private UUID professional;

    @Column(name = "appointment_uuid", nullable = false)
    private UUID appointment;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    // getters/setters
    public UUID getId() {
        return id;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public UUID getCustomer() {
        return customer;
    }

    public UUID getProfessional() {
        return professional;
    }

    public UUID getAppointment() {
        return appointment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCustomer(UUID customer) {
        this.customer = customer;
    }

    public void setProfessional(UUID professional) {
        this.professional = professional;
    }

    public void setAppointment(UUID appointment) {
        this.appointment = appointment;
    }
}
