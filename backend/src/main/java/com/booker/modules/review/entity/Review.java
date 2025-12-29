package com.booker.modules.review.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

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

    @Column(nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_review_customer"))
    private UUID customer;

    @Column(nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_review_professional"))
    private UUID professional;

    @Column(nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_review_appointment"))
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
