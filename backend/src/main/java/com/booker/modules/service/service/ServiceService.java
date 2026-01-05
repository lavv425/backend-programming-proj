package com.booker.modules.service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.service.dto.ServiceDto;
import com.booker.modules.service.dto.ServiceUpsertRequest;
import com.booker.modules.service.entity.Service;
import com.booker.modules.service.repository.ServiceRepository;
import com.booker.utils.base.Response;

/**
 * Service that handles the business logic for service offerings.
 * Manages services that professionals provide to customers, including pricing and availability.
 */
@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /**
     * Retrieves all services from the database.
     *
     * @return a response containing the list of all services
     */
    public Response<List<ServiceDto>> list() {
        List<ServiceDto> data = serviceRepository.findAll().stream()
                .map(ServiceService::toDto)
                .toList();
        return new Response<>(true, data, SuccessCodes.OK);
    }

    /**
     * Retrieves a specific service by its unique identifier.
     *
     * @param id the unique identifier of the service
     * @return a response containing the service if found, or an error if not found
     */
    public Response<ServiceDto> getById(@NonNull UUID id) {
        return serviceRepository.findById(id)
                .map(s -> new Response<>(true, toDto(s), SuccessCodes.OK))
                .orElseGet(() -> new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND));
    }

    /**
     * Creates a new service offering in the system.
     *
     * @param req the request containing the service details including name, description, duration, and price
     * @return a response containing the newly created service, or an error if a service with that name already exists
     */
    public Response<ServiceDto> create(ServiceUpsertRequest req) {
        if (serviceRepository.existsByName(req.name.trim())) {
            return new Response<>(false, null, ErrorCodes.DUPLICATE_RESOURCE);
        }

        Service service = new Service();
        service.setName(req.name.trim());
        service.setDescription(req.description.trim());
        service.setDurationInMinutes(req.durationInMinutes);
        service.setPrice(req.price);
        service.setProfessional(req.professional);
        service.setActive(req.active != null ? req.active : Boolean.TRUE);

        Service saved = serviceRepository.save(service);
        return new Response<>(true, toDto(saved), SuccessCodes.SERVICE_ADDED);
    }

    /**
     * Updates an existing service with new information.
     *
     * @param id the unique identifier of the service to update
     * @param req the request containing the updated service details
     * @return a response containing the updated service, or an error if the service is not found
     */
    public Response<ServiceDto> update(@NonNull UUID id, ServiceUpsertRequest req) {
        Service service = serviceRepository.findById(id).orElse(null);
        if (service == null) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }

        service.setName(req.name.trim());
        service.setDescription(req.description.trim());
        service.setDurationInMinutes(req.durationInMinutes);
        service.setPrice(req.price);
        service.setProfessional(req.professional);
        service.setActive(req.active != null ? req.active : service.getActive());

        Service saved = serviceRepository.save(service);
        return new Response<>(true, toDto(saved), SuccessCodes.SERVICE_UPDATED);
    }

    /**
     * Deletes a service from the system.
     *
     * @param id the unique identifier of the service to delete
     * @return a response indicating success or failure of the deletion
     */
    public Response<Void> delete(@NonNull UUID id) {
        if (!serviceRepository.existsById(id)) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }
        serviceRepository.deleteById(id);
        return new Response<>(true, null, SuccessCodes.SERVICE_DELETED);
    }

    private static ServiceDto toDto(Service s) {
        return new ServiceDto(
                s.getId(),
                s.getName(),
                s.getDescription(),
                s.getDurationInMinutes(),
                s.getPrice(),
                s.getProfessional(),
                s.getActive(),
                s.getCreatedAt());
    }
}
