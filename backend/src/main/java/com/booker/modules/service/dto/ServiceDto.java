package com.booker.modules.service.dto;

import java.time.Instant;
import java.util.UUID;

public class ServiceDto {
    public UUID id;
    public String name;
    public String description;
    public Integer durationInMinutes;
    public Double price;
    public UUID professional;
    public Boolean active;
    public Instant createdAt;

    public ServiceDto(UUID id, String name, String description, Integer durationInMinutes, Double price, UUID professional,
            Boolean active, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationInMinutes = durationInMinutes;
        this.price = price;
        this.professional = professional;
        this.active = active;
        this.createdAt = createdAt;
    }
}
