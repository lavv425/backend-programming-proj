package com.booker.modules.payment.service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.appointment.entity.Appointment;
import com.booker.modules.appointment.repository.AppointmentRepository;
import com.booker.modules.customer.entity.Customer;
import com.booker.modules.customer.repository.CustomerRepository;
import com.booker.modules.log.service.LoggerService;
import com.booker.modules.payment.dto.PaymentDto;
import com.booker.modules.payment.dto.PaymentUpsertRequest;
import com.booker.modules.payment.entity.Payment;
import com.booker.modules.payment.repository.PaymentRepository;
import com.booker.services.EmailService;
import com.booker.services.StripeService;
import com.booker.utils.base.Response;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private StripeService stripeService;

    @Mock
    private EmailService emailService;

    @Mock
    private LoggerService loggerService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void list_shouldReturnAllPayments() {
        Payment payment1 = createTestPayment();
        Payment payment2 = createTestPayment();
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));

        Response<List<PaymentDto>> response = paymentService.list();

        assertTrue(response.status);
        assertEquals(SuccessCodes.OK, response.message);
        assertEquals(2, response.data.size());
        verify(paymentRepository).findAll();
    }

    @Test
    void getById_whenPaymentExists_shouldReturnPayment() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = createTestPayment();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        Response<PaymentDto> response = paymentService.getById(paymentId);

        assertTrue(response.status);
        assertEquals(SuccessCodes.OK, response.message);
        assertNotNull(response.data);
        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void getById_whenPaymentNotFound_shouldReturnError() {
        UUID paymentId = UUID.randomUUID();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        Response<PaymentDto> response = paymentService.getById(paymentId);

        assertFalse(response.status);
        assertEquals(ErrorCodes.RESOURCE_NOT_FOUND, response.message);
        assertNull(response.data);
    }

    @Test
    void create_shouldCreatePaymentAndProcessWithStripe() throws StripeException {
        PaymentUpsertRequest request = new PaymentUpsertRequest();
        request.amount = 100.00;
        request.currency = "USD";
        request.status = "PENDING";
        request.provider = "STRIPE";
        request.appointment = UUID.randomUUID();
        
        Appointment appointment = new Appointment();
        appointment.setCustomer(UUID.randomUUID());
        
        Customer customer = new Customer();
        customer.setEmail("customer@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        
        Payment savedPayment = createTestPayment();
        savedPayment.setStripePaymentIntentId("pi_test123");
        savedPayment.setStatus("succeeded");
        savedPayment.setAppointment(request.appointment);
        
        PaymentIntent paymentIntent = mock(PaymentIntent.class);
        when(paymentIntent.getId()).thenReturn("pi_test123");
        when(paymentIntent.getStatus()).thenReturn("succeeded");
        
        when(stripeService.createPaymentIntentFromBooking(any(), anyString(), anyString(), anyString(), any())).thenReturn(paymentIntent);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(appointmentRepository.findById(request.appointment)).thenReturn(Optional.of(appointment));
        when(customerRepository.findById(appointment.getCustomer())).thenReturn(Optional.of(customer));

        Response<PaymentDto> response = paymentService.create(request);

        assertTrue(response.status);
        assertEquals(SuccessCodes.PAYMENT_PROCESSED, response.message);
        verify(paymentRepository).save(any(Payment.class));
        verify(stripeService).createPaymentIntentFromBooking(any(), anyString(), anyString(), anyString(), any());
        verify(emailService).sendPaymentConfirmation(anyString(), anyString(), anyString(), anyString());
        verify(loggerService).success(anyString(), anyString());
    }

    @Test
    void create_whenStripeException_shouldReturnPaymentFailed() throws StripeException {
        PaymentUpsertRequest request = new PaymentUpsertRequest();
        request.amount = 100.00;
        request.currency = "USD";
        request.status = "PENDING";
        request.provider = "STRIPE";
        request.appointment = UUID.randomUUID();
        
        Appointment appointment = new Appointment();
        appointment.setCustomer(UUID.randomUUID());
        
        Customer customer = new Customer();
        customer.setEmail("customer@example.com");
        
        Payment savedPayment = createTestPayment();
        savedPayment.setStatus("failed");
        
        when(stripeService.createPaymentIntentFromBooking(any(), anyString(), anyString(), anyString(), any()))
                .thenThrow(new StripeException("Payment failed", "req_123", "code", 400) {});
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        Response<PaymentDto> response = paymentService.create(request);

        assertTrue(response.status);
        assertEquals(SuccessCodes.PAYMENT_PROCESSED, response.message);
        verify(paymentRepository).save(any(Payment.class));
        verify(loggerService).error(anyString(), anyString());
        verify(loggerService).success(anyString(), anyString());
    }

    @Test
    void delete_whenPaymentExists_shouldRefundAndDelete() throws StripeException {
        UUID paymentId = UUID.randomUUID();
        Payment payment = createTestPayment();
        payment.setStripePaymentIntentId("pi_test123");
        payment.setStatus("succeeded");
        payment.setAppointment(UUID.randomUUID());
        
        Appointment appointment = new Appointment();
        appointment.setCustomer(UUID.randomUUID());
        
        Customer customer = new Customer();
        customer.setEmail("customer@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(appointmentRepository.findById(payment.getAppointment())).thenReturn(Optional.of(appointment));
        when(customerRepository.findById(appointment.getCustomer())).thenReturn(Optional.of(customer));

        Response<Void> response = paymentService.delete(paymentId);

        assertTrue(response.status);
        assertEquals(SuccessCodes.PAYMENT_DELETED, response.message);
        verify(stripeService).refundPaymentIntent("pi_test123");
        verify(paymentRepository).deleteById(paymentId);
        verify(emailService).sendRefundConfirmation(anyString(), anyString(), anyString(), anyString());
        verify(loggerService).success(anyString(), anyString());
    }

    @Test
    void delete_whenPaymentNotFound_shouldReturnError() {
        UUID paymentId = UUID.randomUUID();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        Response<Void> response = paymentService.delete(paymentId);

        assertFalse(response.status);
        assertEquals(ErrorCodes.RESOURCE_NOT_FOUND, response.message);
    }

    private Payment createTestPayment() {
        Payment payment = new Payment();
        payment.setAmount(100.00);
        payment.setCurrency("USD");
        payment.setStatus("succeeded");
        payment.setProvider("STRIPE");
        payment.setAppointment(UUID.randomUUID());
        payment.setStripePaymentIntentId("pi_test123");
        return payment;
    }
}
