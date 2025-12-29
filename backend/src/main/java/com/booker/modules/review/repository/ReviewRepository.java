package com.booker.modules.review.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.modules.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByProfessional(UUID professionalId);
    List<Review> findByCustomer(UUID customerId);
    List<Review> findByAppointment(UUID appointmentId);
    List<Review> findByRating(Integer rating);
    Optional<Review> findByIdAndCustomerId(UUID reviewId, UUID customerId);
    List<Review> findByCommentContaining(String keyword);
    List<Review> orderByCreatedAtAsc();
    List<Review> orderByCreatedAtDesc();
    List<Review> orderByRatingAsc();
    List<Review> orderByRatingDesc();
}
