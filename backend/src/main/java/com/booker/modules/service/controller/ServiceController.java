package com.booker.modules.service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.booker.constants.routes.Namespaces;
import com.booker.constants.routes.Routes;
import com.booker.modules.service.dto.ServiceDto;
import com.booker.modules.service.dto.ServiceUpsertRequest;
import com.booker.modules.service.service.ServiceService;
import com.booker.utils.base.Response;
import com.booker.utils.base.ResponseEntityBuilder;

import jakarta.validation.Valid;

/**
 * REST controller that handles service management operations.
 * Provides endpoints for professionals to create, manage, and delete their offered services.
 */
@RestController
@RequestMapping(Namespaces.SERVICES)
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    /**
     * Retrieves a list of all available services.
     *
     * @return a response entity containing the list of services
     */
    @GetMapping(Routes.ROOT)
    public ResponseEntity<Response<List<ServiceDto>>> list() {
        return ResponseEntityBuilder.build(serviceService.list());
    }

    /**
     * Retrieves a specific service by its unique identifier.
     *
     * @param id the unique identifier of the service
     * @return a response entity containing the requested service
     */
    @GetMapping(Routes.BY_ID)
    public ResponseEntity<Response<ServiceDto>> getById(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(serviceService.getById(id));
    }

    /**
     * Creates a new service offering with details like name, description, duration, and price.
     *
     * @param req the request containing the service details
     * @return a response entity containing the newly created service
     */
    @PostMapping(Routes.ROOT)
    public ResponseEntity<Response<ServiceDto>> create(@Valid @RequestBody ServiceUpsertRequest req) {
        return ResponseEntityBuilder.build(serviceService.create(req));
    }

    /**
     * Updates an existing service with new information.
     *
     * @param id the unique identifier of the service to update
     * @param req the request containing the updated service details
     * @return a response entity containing the updated service
     */
    @PreAuthorize("@ownershipChecker.isAdmin(authentication) or @ownershipChecker.isServiceOwner(authentication, #id)")
    @PutMapping(Routes.BY_ID)
    public ResponseEntity<Response<ServiceDto>> update(@PathVariable @NonNull UUID id, @Valid @RequestBody ServiceUpsertRequest req) {
        return ResponseEntityBuilder.build(serviceService.update(id, req));
    }

    /**
     * Deletes a service from the system.
     *
     * @param id the unique identifier of the service to delete
     * @return a response entity indicating the result of the deletion
     */
    @PreAuthorize("@ownershipChecker.isAdmin(authentication) or @ownershipChecker.isServiceOwner(authentication, #id)")
    @DeleteMapping(Routes.BY_ID)
    public ResponseEntity<Response<Void>> delete(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(serviceService.delete(id));
    }
}
