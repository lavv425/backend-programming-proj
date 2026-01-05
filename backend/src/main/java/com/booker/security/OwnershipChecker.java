package com.booker.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.booker.modules.appointment.repository.AppointmentRepository;
import com.booker.modules.log.service.LoggerService;
import com.booker.modules.payment.repository.PaymentRepository;
import com.booker.modules.review.repository.ReviewRepository;
import com.booker.modules.service.repository.ServiceRepository;

/**
 * Checks if the authenticated user owns a specific resource.
 * Used with @PreAuthorize to ensure users can only modify their own data.
 */
@Component("ownershipChecker")
public class OwnershipChecker {

    private final AppointmentRepository appointmentRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;
    private final ServiceRepository serviceRepository;
    private final LoggerService loggerService;

    public OwnershipChecker(
            AppointmentRepository appointmentRepository,
            ReviewRepository reviewRepository,
            PaymentRepository paymentRepository,
            ServiceRepository serviceRepository,
            LoggerService loggerService) {
        this.appointmentRepository = appointmentRepository;
        this.reviewRepository = reviewRepository;
        this.paymentRepository = paymentRepository;
        this.serviceRepository = serviceRepository;
        this.loggerService = loggerService;
    }

    /**
     * Checks if the authenticated user is the owner of their profile.
     */
    public boolean isOwner(Authentication authentication, UUID resourceId) {
        UUID userId = getUserIdFromAuth(authentication);
        return userId != null && userId.equals(resourceId);
    }

    /**
     * Checks if the authenticated user is the customer who made the appointment.
     */
    public boolean isAppointmentOwner(Authentication authentication, UUID appointmentId) {
        UUID userId = getUserIdFromAuth(authentication);
        if (userId == null) return false;

        return appointmentRepository.findById(appointmentId)
                .map(appointment -> userId.equals(appointment.getCustomer()))
                .orElse(false);
    }

    /**
     * Checks if the authenticated user is the professional who provides the service.
     */
    public boolean isServiceOwner(Authentication authentication, UUID serviceId) {
        UUID userId = getUserIdFromAuth(authentication);
        if (userId == null) return false;

        return serviceRepository.findById(serviceId)
                .map(service -> userId.equals(service.getProfessional()))
                .orElse(false);
    }

    /**
     * Checks if the authenticated user is the customer who wrote the review.
     */
    public boolean isReviewOwner(Authentication authentication, UUID reviewId) {
        UUID userId = getUserIdFromAuth(authentication);
        if (userId == null) return false;

        return reviewRepository.findById(reviewId)
                .map(review -> userId.equals(review.getCustomer()))
                .orElse(false);
    }

    /**
     * Checks if the authenticated user has admin role.
     */
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String scope = jwt.getClaim("scope");
        return "ADMIN".equals(scope);
    }

    /**
     * Checks if user is either admin or the owner of the resource.
     */
    public boolean isAdminOrOwner(Authentication authentication, UUID resourceId) {
        return isAdmin(authentication) || isOwner(authentication, resourceId);
    }

    /**
     * Extracts user ID from JWT token.
     */
    private UUID getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        try {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String subject = jwt.getSubject();
            return UUID.fromString(subject);
        } catch (Exception e) {
            loggerService.error("Failed to extract userId from JWT: " + e.getMessage(), "OwnershipChecker");
            return null;
        }
    }
}
