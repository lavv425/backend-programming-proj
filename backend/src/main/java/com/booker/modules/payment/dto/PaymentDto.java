package com.booker.modules.payment.dto;

import java.time.Instant;
import java.util.UUID;

public class PaymentDto {
    public UUID id;
    public Double amount;
    public String currency;
    public String status;
    public String provider;
    public UUID appointment;
    public Instant createdAt;

    public PaymentDto(UUID id, Double amount, String currency, String status, String provider, UUID appointment,
            Instant createdAt) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.provider = provider;
        this.appointment = appointment;
        this.createdAt = createdAt;
    }
}
