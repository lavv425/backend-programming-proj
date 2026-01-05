package com.booker.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;

/**
 * Handles all Stripe payment operations for the booking system.
 * Works with both real Stripe API and mock server for development.
 */
@Service
public class StripeService {

    /**
     * Creates a new payment intent on Stripe.
     * 
     * @param amount amount in cents (e.g., 1000 = 10.00 EUR)
     * @param currency currency code (e.g., "eur", "usd")
     * @param description payment description shown in Stripe dashboard
     * @param metadata optional metadata like appointmentId or userId
     * @return the created PaymentIntent
     * @throws StripeException if Stripe API call fails
     */
    public PaymentIntent createPaymentIntent(Long amount, String currency, String description, Map<String, String> metadata) throws StripeException {
        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setDescription(description);

        if (metadata != null && !metadata.isEmpty()) {
            paramsBuilder.putAllMetadata(metadata);
        }

        return PaymentIntent.create(paramsBuilder.build());
    }

    /**
     * Convenience method to create a payment intent from booking data.
     * Automatically converts decimal amounts to cents for Stripe.
     * 
     * @param amount decimal amount that will be converted to cents
     * @param currency currency code
     * @param description payment description
     * @param appointmentId optional appointment ID stored as metadata
     * @param userId optional user ID stored as metadata
     * @return the created PaymentIntent
     * @throws StripeException if Stripe API call fails
     */
    public PaymentIntent createPaymentIntentFromBooking(BigDecimal amount, String currency, String description, String appointmentId, String userId) throws StripeException {
        // Converti BigDecimal in long (centesimi)
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        Map<String, String> metadata = new HashMap<>();
        if (appointmentId != null) {
            metadata.put("appointmentId", appointmentId);
        }
        if (userId != null) {
            metadata.put("userId", userId);
        }

        return createPaymentIntent(amountInCents, currency, description, metadata);
    }

    /**
     * Retrieves an existing payment intent from Stripe.
     * 
     * @param paymentIntentId the payment intent ID
     * @return the PaymentIntent object
     * @throws StripeException if Stripe API call fails
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }

    /**
     * Creates a refund for a payment intent.
     * 
     * @param paymentIntentId the payment intent to refund
     * @param amount amount to refund in cents (null for full refund)
     * @param reason refund reason: "duplicate", "fraudulent", or "requested_by_customer"
     * @return the created Refund
     * @throws StripeException if Stripe API call fails
     */
    public Refund createRefund(String paymentIntentId, Long amount, String reason) throws StripeException {
        RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId);

        if (amount != null) {
            paramsBuilder.setAmount(amount);
        }

        if (reason != null) {
            RefundCreateParams.Reason reasonEnum = switch (reason.toLowerCase()) {
                case "duplicate" -> RefundCreateParams.Reason.DUPLICATE;
                case "fraudulent" -> RefundCreateParams.Reason.FRAUDULENT;
                case "requested_by_customer" -> RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
                default -> null;
            };
            if (reasonEnum != null) {
                paramsBuilder.setReason(reasonEnum);
            }
        }

        return Refund.create(paramsBuilder.build());
    }

    /**
     * Issues a full refund for a payment intent.
     * 
     * @param paymentIntentId the payment intent to refund
     * @return the created Refund
     * @throws StripeException if Stripe API call fails
     */
    public Refund refundPaymentIntent(String paymentIntentId) throws StripeException {
        return createRefund(paymentIntentId, null, "requested_by_customer");
    }
}
