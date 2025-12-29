package com.booker.modules.appointment.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

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

    @Column(nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_appointment_customer"))
    private UUID customer;

    @Column(nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_appointment_professional"))
    private UUID professional;

    @Column(nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_appointment_service"))
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
