package com.booker.modules.payment.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

/**
 * Represents a payment transaction for an appointment.
 * Tracks the amount, currency, payment status, and the payment provider used.
 * Currently supports Stripe as a payment provider.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "payments", indexes = @Index(name = "idx_appointment_email", columnList = "id"))
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String provider;

    @Column(name = "appointment_uuid", nullable = false)
    private UUID appointment;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

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

    public Double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public String getProvider() {
        return provider;
    }

    public UUID getAppointment() {
        return appointment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setAppointment(UUID appointment) {
        this.appointment = appointment;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }
}
