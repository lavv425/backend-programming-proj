package com.booker.modules.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.modules.customer.entity.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    List<Customer> findByLoyaltyPointsGreaterThan(Integer points);
}