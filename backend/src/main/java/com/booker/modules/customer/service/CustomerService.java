package com.booker.modules.customer.service;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.customer.dto.CustomerPublicDto;
import com.booker.modules.customer.entity.Customer;
import com.booker.modules.customer.repository.CustomerRepository;
import com.booker.utils.base.Response;

/**
 * Service that handles the business logic for customer profiles.
 * Provides access to customer information within the booking system.
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Retrieves all customers from the database.
     *
     * @return a response containing the list of all customers with their public profiles
     */
    public Response<List<CustomerPublicDto>> list() {
        List<CustomerPublicDto> data = customerRepository.findAll().stream()
                .map(CustomerService::toDto)
                .toList();
        return new Response<>(true, data, SuccessCodes.OK);
    }

    /**
     * Retrieves detailed information about a specific customer.
     *
     * @param id the unique identifier of the customer
     * @return a response containing the customer's public profile if found, or an error if not found
     */
    public Response<CustomerPublicDto> getById(@NonNull UUID id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }
        return new Response<>(true, toDto(customer), SuccessCodes.OK);
    }

    private static CustomerPublicDto toDto(Customer c) {
        return new CustomerPublicDto(
                c.getId(),
                c.getEmail(),
                c.getFirstName(),
                c.getLastName(),
                c.getRole(),
                c.getProfileImageUrl(),
                c.getCreatedAt(),
                c.getPhoneNumber(),
                c.getLoyaltyPoints()
        );
    }
}
