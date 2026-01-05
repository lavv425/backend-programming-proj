package com.booker.modules.appointment.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a scheduled appointment between a customer and a professional.
 * Each appointment has a specific time slot, links to the service being provided,
 * and tracks its current status (pending, confirmed, cancelled, etc.).
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "appointment", indexes = {
        @Index(name = "idx_appointment_customer", columnList = "customer_uuid"),
        @Index(name = "idx_appointment_professional", columnList = "professional_uuid"),
        @Index(name = "idx_appointment_service", columnList = "service_uuid") })
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private String status;

    @Column(name = "customer_uuid", nullable = false)
    private UUID customer;

    @Column(name = "professional_uuid", nullable = false)
    private UUID professional;

    @Column(name = "service_uuid", nullable = false)
    private UUID service;

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

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public UUID getCustomer() {
        return customer;
    }

    public UUID getProfessional() {
        return professional;
    }

    public UUID getService() {
        return service;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCustomer(UUID customer) {
        this.customer = customer;
    }

    public void setProfessional(UUID professional) {
        this.professional = professional;
    }

    public void setService(UUID service) {
        this.service = service;
    }
}
