package com.booker.modules.review.service;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.review.dto.ReviewDto;
import com.booker.modules.review.dto.ReviewUpsertRequest;
import com.booker.modules.review.entity.Review;
import com.booker.modules.review.repository.ReviewRepository;
import com.booker.utils.base.Response;

/**
 * Service that handles the business logic for customer reviews.
 * Manages review submissions, updates, and retrieval for appointments.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Retrieves all reviews from the database.
     *
     * @return a response containing the list of all reviews
     */
    public Response<List<ReviewDto>> list() {
        List<ReviewDto> data = reviewRepository.findAll().stream()
                .map(ReviewService::toDto)
                .toList();
        return new Response<>(true, data, SuccessCodes.OK);
    }

    /**
     * Retrieves a specific review by its unique identifier.
     *
     * @param id the unique identifier of the review
     * @return a response containing the review if found, or an error if not found
     */
    public Response<ReviewDto> getById(@NonNull UUID id) {
        return reviewRepository.findById(id)
                .map(r -> new Response<>(true, toDto(r), SuccessCodes.OK))
                .orElseGet(() -> new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND));
    }

    /**
     * Creates a new review for a completed appointment.
     *
     * @param req the request containing the review details including rating, comment, and related entities
     * @return a response containing the newly created review
     */
    public Response<ReviewDto> create(ReviewUpsertRequest req) {
        Review review = new Review();
        review.setRating(req.rating);
        review.setComment(req.comment != null ? req.comment.trim() : null);
        review.setCustomer(req.customer);
        review.setProfessional(req.professional);
        review.setAppointment(req.appointment);

        Review saved = reviewRepository.save(review);
        return new Response<>(true, toDto(saved), SuccessCodes.REVIEW_SUBMITTED);
    }

    /**
     * Updates an existing review with new information.
     *
     * @param id the unique identifier of the review to update
     * @param req the request containing the updated review details
     * @return a response containing the updated review, or an error if the review is not found
     */
    public Response<ReviewDto> update(@NonNull UUID id, ReviewUpsertRequest req) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }

        review.setRating(req.rating);
        review.setComment(req.comment != null ? req.comment.trim() : null);
        review.setCustomer(req.customer);
        review.setProfessional(req.professional);
        review.setAppointment(req.appointment);

        Review saved = reviewRepository.save(review);
        return new Response<>(true, toDto(saved), SuccessCodes.REVIEW_UPDATED);
    }

    /**
     * Deletes a review from the system.
     *
     * @param id the unique identifier of the review to delete
     * @return a response indicating success or failure of the deletion
     */
    public Response<Void> delete(@NonNull UUID id) {
        if (!reviewRepository.existsById(id)) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }
        reviewRepository.deleteById(id);
        return new Response<>(true, null, SuccessCodes.REVIEW_DELETED);
    }

    private static ReviewDto toDto(Review r) {
        return new ReviewDto(
                r.getId(),
                r.getRating(),
                r.getComment(),
                r.getCustomer(),
                r.getProfessional(),
                r.getAppointment(),
                r.getCreatedAt());
    }
}
