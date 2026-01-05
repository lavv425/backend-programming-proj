package com.booker.modules.review.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReviewUpsertRequest {

    @NotNull
    @Min(1)
    @Max(5)
    public Integer rating;

    public String comment;

    @NotNull
    public UUID customer;

    @NotNull
    public UUID professional;

    @NotNull
    public UUID appointment;
}
