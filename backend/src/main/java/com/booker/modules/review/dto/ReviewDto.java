package com.booker.modules.review.dto;

import java.time.Instant;
import java.util.UUID;

public class ReviewDto {
    public UUID id;
    public Integer rating;
    public String comment;
    public UUID customer;
    public UUID professional;
    public UUID appointment;
    public Instant createdAt;

    public ReviewDto(UUID id, Integer rating, String comment, UUID customer, UUID professional, UUID appointment,
            Instant createdAt) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.customer = customer;
        this.professional = professional;
        this.appointment = appointment;
        this.createdAt = createdAt;
    }
}
