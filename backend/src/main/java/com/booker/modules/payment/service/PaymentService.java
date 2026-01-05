package com.booker.modules.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.payment.dto.PaymentDto;
import com.booker.modules.payment.dto.PaymentUpsertRequest;
import com.booker.modules.payment.entity.Payment;
import com.booker.modules.payment.repository.PaymentRepository;
import com.booker.modules.appointment.repository.AppointmentRepository;
import com.booker.modules.appointment.entity.Appointment;
import com.booker.modules.customer.repository.CustomerRepository;
import com.booker.modules.customer.entity.Customer;
import com.booker.services.EmailService;
import com.booker.services.StripeService;
import com.booker.modules.log.service.LoggerService;
import com.booker.utils.base.Response;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

/**
 * Manages payment processing and transaction records.
 * 
 * This service handles payment creation, updates, and deletions, integrating with
 * Stripe for payment processing. It creates PaymentIntents for new payments and
 * can issue refunds when payments are deleted. Email notifications are sent for
 * successful payments and refunds.
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final StripeService stripeService;
    private final EmailService emailService;
    private final LoggerService loggerService;

    public PaymentService(PaymentRepository paymentRepository, AppointmentRepository appointmentRepository, CustomerRepository customerRepository, StripeService stripeService, EmailService emailService, LoggerService loggerService) {
        this.paymentRepository = paymentRepository;
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.stripeService = stripeService;
        this.emailService = emailService;
        this.loggerService = loggerService;
    }

    /**
     * Retrieves all payment records in the system.
     * 
     * @return a response containing a list of all payments
     */
    public Response<List<PaymentDto>> list() {
        List<PaymentDto> data = paymentRepository.findAll().stream()
                .map(PaymentService::toDto)
                .toList();
        return new Response<>(true, data, SuccessCodes.OK);
    }

    /**
     * Retrieves a specific payment by its unique identifier.
     * 
     * @param id the unique identifier of the payment
     * @return a response containing the payment details or an error if not found
     */
    public Response<PaymentDto> getById(@NonNull UUID id) {
        return paymentRepository.findById(id)
                .map(p -> new Response<>(true, toDto(p), SuccessCodes.OK))
                .orElseGet(() -> new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND));
    }

    /**
     * Creates a new payment record and initiates payment processing with Stripe.
     * 
     * Creates a PaymentIntent on Stripe for the specified amount and currency.
     * If the payment processing succeeds, sends a confirmation email to the customer.
     * If Stripe integration fails, the payment is saved with a "failed" status.
     * 
     * @param req the payment creation request containing amount, currency, and appointment details
     * @return a response containing the created payment data with its Stripe status
     */
    public Response<PaymentDto> create(PaymentUpsertRequest req) {
        Payment payment = new Payment();
        payment.setAmount(req.amount);
        payment.setCurrency(req.currency.trim());
        payment.setProvider(req.provider.trim());
        payment.setAppointment(req.appointment);

        // Crea PaymentIntent su Stripe
        try {
            BigDecimal amount = BigDecimal.valueOf(req.amount);
            String description = "Payment for appointment " + req.appointment;
            
            PaymentIntent paymentIntent = stripeService.createPaymentIntentFromBooking(
                amount,
                req.currency,
                description,
                req.appointment.toString(),
                null // userId - da recuperare se disponibile
            );
            
            payment.setStripePaymentIntentId(paymentIntent.getId());
            payment.setStatus(paymentIntent.getStatus()); // "requires_payment_method", "succeeded", etc.
        } catch (StripeException e) {
            // Fallback: salva comunque il payment con status failed
            payment.setStatus("failed");
            loggerService.error("Stripe PaymentIntent creation failed: " + e.getMessage(), "PaymentService");
        }

        Payment saved = paymentRepository.save(payment);
        
        loggerService.success("Payment created: " + saved.getId() + " (status: " + saved.getStatus() + ")", "PaymentService");
        
        // Invia email di conferma (se pagamento riuscito)
        if ("succeeded".equals(saved.getStatus())) {
            try {
                Appointment appointment = appointmentRepository.findById(saved.getAppointment()).orElse(null);
                if (appointment != null) {
                    Customer customer = customerRepository.findById(appointment.getCustomer()).orElse(null);
                    if (customer != null && customer.getEmail() != null) {
                        emailService.sendPaymentConfirmation(
                            customer.getEmail(),
                            customer.getFirstName() + " " + customer.getLastName(),
                            String.format("%.2f %s", saved.getAmount(), saved.getCurrency()),
                            "Servizio prenotato"
                        );
                    }
                }
            } catch (Exception e) {
                // Log error ma non fallire l'operazione
                loggerService.error("Failed to send payment confirmation email: " + e.getMessage(), "PaymentService");
            }
        }
        
        return new Response<>(true, toDto(saved), SuccessCodes.PAYMENT_PROCESSED);
    }

    /**
     * Updates an existing payment record.
     * 
     * Allows modification of payment amount, currency, status, provider, and
     * associated appointment. Note that this does not update the Stripe PaymentIntent.
     * 
     * @param id the unique identifier of the payment to update
     * @param req the update request containing the new payment data
     * @return a response containing the updated payment data or an error if not found
     */
    public Response<PaymentDto> update(@NonNull UUID id, PaymentUpsertRequest req) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment == null) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }

        payment.setAmount(req.amount);
        payment.setCurrency(req.currency.trim());
        payment.setStatus(req.status.trim());
        payment.setProvider(req.provider.trim());
        payment.setAppointment(req.appointment);

        Payment saved = paymentRepository.save(payment);
        return new Response<>(true, toDto(saved), SuccessCodes.PAYMENT_UPDATED);
    }

    /**
     * Deletes a payment record and issues a refund if applicable.
     * 
     * If the payment was successfully processed through Stripe, initiates a refund
     * via the Stripe API and sends a refund confirmation email to the customer.
     * The payment record is then permanently deleted from the database.
     * 
     * @param id the unique identifier of the payment to delete
     * @return a response indicating success or an error if the payment is not found
     */
    public Response<Void> delete(@NonNull UUID id) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment == null) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }
        
        // Se c'è un PaymentIntent su Stripe, crea rimborso
        if (payment.getStripePaymentIntentId() != null && "succeeded".equals(payment.getStatus())) {
            try {
                stripeService.refundPaymentIntent(payment.getStripePaymentIntentId());
                
                // Invia email di conferma rimborso
                Appointment appointment = appointmentRepository.findById(payment.getAppointment()).orElse(null);
                if (appointment != null) {
                    Customer customer = customerRepository.findById(appointment.getCustomer()).orElse(null);
                    if (customer != null && customer.getEmail() != null) {
                        emailService.sendRefundConfirmation(
                            customer.getEmail(),
                            customer.getFirstName() + " " + customer.getLastName(),
                            String.format("%.2f %s", payment.getAmount(), payment.getCurrency()),
                            "Servizio"
                        );
                    }
                }
            } catch (StripeException e) {
                // Log error ma procedi comunque con la cancellazione
                loggerService.error("Stripe refund failed for payment " + payment.getId() + ": " + e.getMessage(), "PaymentService");
            }
        }
        
        paymentRepository.deleteById(id);
        loggerService.success("Payment deleted and refunded: " + id, "PaymentService");
        return new Response<>(true, null, SuccessCodes.PAYMENT_DELETED);
    }

    private static PaymentDto toDto(Payment p) {
        return new PaymentDto(
                p.getId(),
                p.getAmount(),
                p.getCurrency(),
                p.getStatus(),
                p.getProvider(),
                p.getAppointment(),
                p.getCreatedAt());
    }
}
