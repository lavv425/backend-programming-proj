package com.booker.modules.service.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ServiceUpsertRequest {

    @NotBlank
    public String name;

    @NotBlank
    public String description;

    @NotNull
    @Min(1)
    public Integer durationInMinutes;

    @NotNull
    @Min(0)
    public Double price;

    @NotNull
    public UUID professional;

    public Boolean active;
}
