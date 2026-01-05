package com.booker.modules.appointment.dto;

import java.time.Instant;
import java.util.UUID;

public class AppointmentDto {
    public UUID id;
    public Instant startTime;
    public Instant endTime;
    public String status;
    public UUID customer;
    public UUID professional;
    public UUID service;
    public Instant createdAt;

    public AppointmentDto(UUID id, Instant startTime, Instant endTime, String status, UUID customer, UUID professional,
            UUID service, Instant createdAt) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.customer = customer;
        this.professional = professional;
        this.service = service;
        this.createdAt = createdAt;
    }
}
