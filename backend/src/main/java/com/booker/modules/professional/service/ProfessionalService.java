package com.booker.modules.professional.service;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.professional.dto.ProfessionalPublicDto;
import com.booker.modules.professional.entity.Professional;
import com.booker.modules.professional.repository.ProfessionalRepository;
import com.booker.utils.base.Response;

/**
 * Service that handles the business logic for professional profiles.
 * Provides access to professional information for customers to view and book appointments.
 */
@Service
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;

    public ProfessionalService(ProfessionalRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    /**
     * Retrieves all professionals from the database.
     *
     * @return a response containing the list of all professionals with their public profiles
     */
    public Response<List<ProfessionalPublicDto>> list() {
        List<ProfessionalPublicDto> data = professionalRepository.findAll().stream()
                .map(ProfessionalService::toDto)
                .toList();
        return new Response<>(true, data, SuccessCodes.OK);
    }

    /**
     * Retrieves detailed information about a specific professional.
     *
     * @param id the unique identifier of the professional
     * @return a response containing the professional's public profile if found, or an error if not found
     */
    public Response<ProfessionalPublicDto> getById(@NonNull UUID id) {
        Professional professional = professionalRepository.findById(id).orElse(null);
        if (professional == null) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }
        return new Response<>(true, toDto(professional), SuccessCodes.OK);
    }

    private static ProfessionalPublicDto toDto(Professional p) {
        return new ProfessionalPublicDto(
                p.getId(),
                p.getEmail(),
                p.getFirstName(),
                p.getLastName(),
                p.getRole(),
                p.getProfileImageUrl(),
                p.getCreatedAt(),
                p.getBio(),
                p.getYearsOfExperience(),
                p.getIsVerified(),
                p.getAverageRating()
        );
    }
}
