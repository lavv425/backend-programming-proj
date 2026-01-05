package com.booker.modules.review.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.booker.constants.routes.Namespaces;
import com.booker.constants.routes.Routes;
import com.booker.modules.review.dto.ReviewDto;
import com.booker.modules.review.dto.ReviewUpsertRequest;
import com.booker.modules.review.service.ReviewService;
import com.booker.utils.base.Response;
import com.booker.utils.base.ResponseEntityBuilder;

import jakarta.validation.Valid;

/**
 * REST controller that manages customer reviews for professionals.
 * Provides endpoints for customers to submit, view, update, and delete reviews.
 */
@RestController
@RequestMapping(Namespaces.REVIEWS)
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Retrieves a list of all reviews in the system.
     *
     * @return a response entity containing the list of reviews
     */
    @GetMapping(Routes.ROOT)
    public ResponseEntity<Response<List<ReviewDto>>> list() {
        return ResponseEntityBuilder.build(reviewService.list());
    }

    /**
     * Retrieves a specific review by its unique identifier.
     *
     * @param id the unique identifier of the review
     * @return a response entity containing the requested review
     */
    @GetMapping(Routes.BY_ID)
    public ResponseEntity<Response<ReviewDto>> getById(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(reviewService.getById(id));
    }

    /**
     * Creates a new review for a completed appointment.
     *
     * @param req the request containing the review details including rating and comment
     * @return a response entity containing the newly created review
     */
    @PostMapping(Routes.ROOT)
    public ResponseEntity<Response<ReviewDto>> create(@Valid @RequestBody ReviewUpsertRequest req) {
        return ResponseEntityBuilder.build(reviewService.create(req));
    }

    /**
     * Updates an existing review with new information.
     *
     * @param id the unique identifier of the review to update
     * @param req the request containing the updated review details
     * @return a response entity containing the updated review
     */
    @PreAuthorize("@ownershipChecker.isAdmin(authentication) or @ownershipChecker.isReviewOwner(authentication, #id)")
    @PutMapping(Routes.BY_ID)
    public ResponseEntity<Response<ReviewDto>> update(@PathVariable @NonNull UUID id, @Valid @RequestBody ReviewUpsertRequest req) {
        return ResponseEntityBuilder.build(reviewService.update(id, req));
    }

    /**
     * Deletes a review from the system.
     *
     * @param id the unique identifier of the review to delete
     * @return a response entity indicating the result of the deletion
     */
    @PreAuthorize("@ownershipChecker.isAdmin(authentication) or @ownershipChecker.isReviewOwner(authentication, #id)")
    @DeleteMapping(Routes.BY_ID)
    public ResponseEntity<Response<Void>> delete(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(reviewService.delete(id));
    }
}
