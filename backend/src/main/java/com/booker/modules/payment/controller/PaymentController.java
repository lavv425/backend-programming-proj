package com.booker.modules.payment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.booker.constants.routes.Namespaces;
import com.booker.constants.routes.Routes;
import com.booker.modules.payment.dto.PaymentDto;
import com.booker.modules.payment.dto.PaymentUpsertRequest;
import com.booker.modules.payment.service.PaymentService;
import com.booker.utils.base.Response;
import com.booker.utils.base.ResponseEntityBuilder;

import jakarta.validation.Valid;

/**
 * Handles payment operations.
 * Provides endpoints to create, view, update, and delete payment records.
 */
@RestController
@RequestMapping(Namespaces.PAYMENTS)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Gets all payments in the system.
     */
    @GetMapping(Routes.ROOT)
    public ResponseEntity<Response<List<PaymentDto>>> list() {
        return ResponseEntityBuilder.build(paymentService.list());
    }

    /**
     * Gets a specific payment by its ID.
     */
    @GetMapping(Routes.BY_ID)
    public ResponseEntity<Response<PaymentDto>> getById(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(paymentService.getById(id));
    }

    /**
     * Creates a new payment record.
     */
    @PostMapping(Routes.ROOT)
    public ResponseEntity<Response<PaymentDto>> create(@Valid @RequestBody PaymentUpsertRequest req) {
        return ResponseEntityBuilder.build(paymentService.create(req));
    }

    /**
     * Updates an existing payment record.
     */
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping(Routes.BY_ID)
    public ResponseEntity<Response<PaymentDto>> update(@PathVariable @NonNull UUID id, @Valid @RequestBody PaymentUpsertRequest req) {
        return ResponseEntityBuilder.build(paymentService.update(id, req));
    }

    /**
     * Deletes a payment record.
     */
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping(Routes.BY_ID)
    public ResponseEntity<Response<Void>> delete(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(paymentService.delete(id));
    }
}
