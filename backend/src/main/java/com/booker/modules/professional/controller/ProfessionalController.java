package com.booker.modules.professional.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import com.booker.constants.routes.Namespaces;
import com.booker.constants.routes.Routes;
import com.booker.modules.professional.dto.ProfessionalPublicDto;
import com.booker.modules.professional.service.ProfessionalService;
import com.booker.utils.base.Response;
import com.booker.utils.base.ResponseEntityBuilder;

/**
 * REST controller that provides public access to professional profiles.
 * Allows customers to view professionals and their information for booking appointments.
 */
@RestController
@RequestMapping(Namespaces.PROFESSIONALS)
public class ProfessionalController {

    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    /**
     * Retrieves a list of all professionals available for booking.
     *
     * @return a response entity containing the list of professionals with their public profiles
     */
    @GetMapping(Routes.ROOT)
    public ResponseEntity<Response<List<ProfessionalPublicDto>>> list() {
        return ResponseEntityBuilder.build(professionalService.list());
    }

    /**
     * Retrieves detailed information about a specific professional.
     *
     * @param id the unique identifier of the professional
     * @return a response entity containing the professional's public profile
     */
    @GetMapping(Routes.BY_ID)
    public ResponseEntity<Response<ProfessionalPublicDto>> getById(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(professionalService.getById(id));
    }
}
