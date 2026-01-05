package com.booker.modules.appointment.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AppointmentUpsertRequest {

    @NotNull
    public Instant startTime;

    @NotNull
    public Instant endTime;

    @NotBlank
    public String status;

    @NotNull
    public UUID customer;

    @NotNull
    public UUID professional;

    @NotNull
    public UUID service;
}
