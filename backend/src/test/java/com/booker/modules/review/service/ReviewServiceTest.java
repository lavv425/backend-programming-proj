package com.booker.modules.review.service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.review.dto.ReviewDto;
import com.booker.modules.review.dto.ReviewUpsertRequest;
import com.booker.modules.review.entity.Review;
import com.booker.modules.review.repository.ReviewRepository;
import com.booker.utils.base.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void list_shouldReturnAllReviews() {
        Review review1 = createTestReview();
        Review review2 = createTestReview();
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(review1, review2));

        Response<List<ReviewDto>> response = reviewService.list();

        assertTrue(response.status);
        assertEquals(SuccessCodes.OK, response.message);
        assertEquals(2, response.data.size());
        verify(reviewRepository).findAll();
    }

    @Test
    void getById_whenReviewExists_shouldReturnReview() {
        UUID reviewId = UUID.randomUUID();
        Review review = createTestReview();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Response<ReviewDto> response = reviewService.getById(reviewId);

        assertTrue(response.status);
        assertEquals(SuccessCodes.OK, response.message);
        assertNotNull(response.data);
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void getById_whenReviewNotFound_shouldReturnError() {
        UUID reviewId = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Response<ReviewDto> response = reviewService.getById(reviewId);

        assertFalse(response.status);
        assertEquals(ErrorCodes.RESOURCE_NOT_FOUND, response.message);
        assertNull(response.data);
    }

    @Test
    void create_shouldCreateReview() {
        ReviewUpsertRequest request = new ReviewUpsertRequest();
        request.rating = 5;
        request.comment = "Excellent service!";
        request.customer = UUID.randomUUID();
        request.professional = UUID.randomUUID();
        request.appointment = UUID.randomUUID();
        
        Review savedReview = createTestReview();
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        Response<ReviewDto> response = reviewService.create(request);

        assertTrue(response.status);
        assertEquals(SuccessCodes.REVIEW_SUBMITTED, response.message);
        assertNotNull(response.data);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void update_whenReviewExists_shouldUpdateReview() {
        UUID reviewId = UUID.randomUUID();
        Review existingReview = createTestReview();
        
        ReviewUpsertRequest request = new ReviewUpsertRequest();
        request.rating = 4;
        request.comment = "Updated comment";
        request.customer = UUID.randomUUID();
        request.professional = UUID.randomUUID();
        request.appointment = UUID.randomUUID();
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(existingReview);

        Response<ReviewDto> response = reviewService.update(reviewId, request);

        assertTrue(response.status);
        assertEquals(SuccessCodes.REVIEW_UPDATED, response.message);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void update_whenReviewNotFound_shouldReturnError() {
        UUID reviewId = UUID.randomUUID();
        ReviewUpsertRequest request = new ReviewUpsertRequest();
        request.rating = 4;
        request.comment = "Updated comment";
        request.customer = UUID.randomUUID();
        request.professional = UUID.randomUUID();
        request.appointment = UUID.randomUUID();
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Response<ReviewDto> response = reviewService.update(reviewId, request);

        assertFalse(response.status);
        assertEquals(ErrorCodes.RESOURCE_NOT_FOUND, response.message);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void delete_whenReviewExists_shouldDeleteReview() {
        UUID reviewId = UUID.randomUUID();
        
        when(reviewRepository.existsById(reviewId)).thenReturn(true);

        Response<Void> response = reviewService.delete(reviewId);

        assertTrue(response.status);
        assertEquals(SuccessCodes.REVIEW_DELETED, response.message);
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void delete_whenReviewNotFound_shouldReturnError() {
        UUID reviewId = UUID.randomUUID();
        when(reviewRepository.existsById(reviewId)).thenReturn(false);

        Response<Void> response = reviewService.delete(reviewId);

        assertFalse(response.status);
        assertEquals(ErrorCodes.RESOURCE_NOT_FOUND, response.message);
    }

    private Review createTestReview() {
        Review review = new Review();
        review.setRating(5);
        review.setComment("Great service!");
        review.setCustomer(UUID.randomUUID());
        review.setProfessional(UUID.randomUUID());
        review.setAppointment(UUID.randomUUID());
        return review;
    }
}
