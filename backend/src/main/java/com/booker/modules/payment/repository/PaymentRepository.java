package com.booker.modules.payment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.modules.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByAppointment(UUID appointmentId);
    List<Payment> findByStatus(String status);
    List<Payment> findByProvider(String provider);
    List<Payment> findByAmountGreaterThan(Double amount);
    List<Payment> findByAmountLessThan(Double amount);
    List<Payment> findByCurrency(String currency);
    List<Payment> orderByAmountAsc();
    List<Payment> orderByAmountDesc();
    List<Payment> orderByCreatedAtAsc();
    List<Payment> orderByCreatedAtDesc();
}
