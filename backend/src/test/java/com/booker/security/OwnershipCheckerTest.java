package com.booker.security;

import com.booker.modules.appointment.entity.Appointment;
import com.booker.modules.appointment.repository.AppointmentRepository;
import com.booker.modules.log.service.LoggerService;
import com.booker.modules.review.entity.Review;
import com.booker.modules.review.repository.ReviewRepository;
import com.booker.modules.service.entity.Service;
import com.booker.modules.service.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnershipCheckerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private LoggerService loggerService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OwnershipChecker ownershipChecker;

    @Test
    void isOwner_whenUserOwnsResource_shouldReturnTrue() {
        UUID userId = UUID.randomUUID();
        Jwt jwt = createMockJwt(userId.toString());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        boolean result = ownershipChecker.isOwner(authentication, userId);

        assertTrue(result);
    }

    @Test
    void isOwner_whenUserDoesNotOwnResource_shouldReturnFalse() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        Jwt jwt = createMockJwt(userId.toString());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        boolean result = ownershipChecker.isOwner(authentication, otherUserId);

        assertFalse(result);
    }

    @Test
    void isAppointmentOwner_whenUserOwnsAppointment_shouldReturnTrue() {
        UUID userId = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();
        Jwt jwt = createMockJwt(userId.toString());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        Appointment appointment = new Appointment();
        appointment.setCustomer(userId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        boolean result = ownershipChecker.isAppointmentOwner(authentication, appointmentId);

        assertTrue(result);
        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    void isAppointmentOwner_whenUserDoesNotOwnAppointment_shouldReturnFalse() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();
        Jwt jwt = createMockJwt(userId.toString());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        Appointment appointment = new Appointment();
        appointment.setCustomer(otherUserId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        boolean result = ownershipChecker.isAppointmentOwner(authentication, appointmentId);

        assertFalse(result);
        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    void isReviewOwner_whenUserOwnsReview_shouldReturnTrue() {
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        Jwt jwt = createMockJwt(userId.toString());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        Review review = new Review();
        review.setCustomer(userId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        boolean result = ownershipChecker.isReviewOwner(authentication, reviewId);

        assertTrue(result);
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void isReviewOwner_whenReviewNotFound_shouldReturnFalse() {
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        Jwt jwt = createMockJwt(userId.toString());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        boolean result = ownershipChecker.isReviewOwner(authentication, reviewId);

        assertFalse(result);
    }

    @Test
    void isServiceOwner_whenUserOwnsService_shouldReturnTrue() {
        UUID userId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        Jwt jwt = createMockJwt(userId.toString());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        Service service = new Service();
        service.setProfessional(userId);
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

        boolean result = ownershipChecker.isServiceOwner(authentication, serviceId);

        assertTrue(result);
        verify(serviceRepository).findById(serviceId);
    }

    private Jwt createMockJwt(String userId) {
        return Jwt.withTokenValue("test-token")
                .header("alg", "HS256")
                .claim("sub", userId)
                .claim("scope", "CUSTOMER")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
