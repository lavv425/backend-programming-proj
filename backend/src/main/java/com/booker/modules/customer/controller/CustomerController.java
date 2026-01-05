package com.booker.modules.customer.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import com.booker.constants.routes.Namespaces;
import com.booker.constants.routes.Routes;
import com.booker.modules.customer.dto.CustomerPublicDto;
import com.booker.modules.customer.service.CustomerService;
import com.booker.utils.base.Response;
import com.booker.utils.base.ResponseEntityBuilder;

/**
 * REST controller that provides access to customer information.
 * Allows viewing customer profiles and details within the system.
 */
@RestController
@RequestMapping(Namespaces.CUSTOMERS)
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Retrieves a list of all registered customers.
     *
     * @return a response entity containing the list of customers with their public profiles
     */
    @GetMapping(Routes.ROOT)
    public ResponseEntity<Response<List<CustomerPublicDto>>> list() {
        return ResponseEntityBuilder.build(customerService.list());
    }

    /**
     * Retrieves detailed information about a specific customer.
     *
     * @param id the unique identifier of the customer
     * @return a response entity containing the customer's public profile
     */
    @GetMapping(Routes.BY_ID)
    public ResponseEntity<Response<CustomerPublicDto>> getById(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(customerService.getById(id));
    }
}
