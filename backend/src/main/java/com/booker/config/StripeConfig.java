package com.booker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;
import com.stripe.net.ApiResource;

import jakarta.annotation.PostConstruct;

/**
 * Configuration class for Stripe payment integration.
 * Sets up the API key and base URL for Stripe operations.
 */
@Configuration
public class StripeConfig {

    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.api-base-url}")
    private String apiBaseUrl;

    /**
     * Initializes the Stripe configuration after the bean is constructed.
     * Sets the API key and optionally overrides the API base URL for testing environments.
     */
    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
        
        // Imposta l'URL base per il mock server (in dev) o API reale (in prod)
        if (!apiBaseUrl.equals("https://api.stripe.com")) {
            Stripe.overrideApiBase(apiBaseUrl);
        }
    }
}
