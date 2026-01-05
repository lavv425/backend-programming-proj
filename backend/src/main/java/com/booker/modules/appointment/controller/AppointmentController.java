package com.booker.modules.appointment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.booker.constants.routes.Namespaces;
import com.booker.constants.routes.Routes;
import com.booker.modules.appointment.dto.AppointmentDto;
import com.booker.modules.appointment.dto.AppointmentUpsertRequest;
import com.booker.modules.appointment.service.AppointmentService;
import com.booker.utils.base.Response;
import com.booker.utils.base.ResponseEntityBuilder;

import jakarta.validation.Valid;

/**
 * Manages appointment bookings.
 * Provides endpoints to create, view, update, and delete appointments.
 */
@RestController
@RequestMapping(Namespaces.APPOINTMENTS)
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	/**
	 * Gets all appointments in the system.
	 */
	@GetMapping(Routes.ROOT)
	public ResponseEntity<Response<List<AppointmentDto>>> list() {
		return ResponseEntityBuilder.build(appointmentService.list());
	}

	/**
	 * Gets a specific appointment by its ID.
	 */
	@GetMapping(Routes.BY_ID)
	public ResponseEntity<Response<AppointmentDto>> getById(@PathVariable @NonNull UUID id) {
		return ResponseEntityBuilder.build(appointmentService.getById(id));
	}

	/**
	 * Creates a new appointment.
	 */
	@PostMapping(Routes.ROOT)
	public ResponseEntity<Response<AppointmentDto>> create(@Valid @RequestBody AppointmentUpsertRequest req) {
		return ResponseEntityBuilder.build(appointmentService.create(req));
	}

	/**
	 * Updates an existing appointment.
	 */
	@PreAuthorize("@ownershipChecker.isAdmin(authentication) or @ownershipChecker.isAppointmentOwner(authentication, #id)")
	@PutMapping(Routes.BY_ID)
	public ResponseEntity<Response<AppointmentDto>> update(@PathVariable @NonNull UUID id, @Valid @RequestBody AppointmentUpsertRequest req) {
		return ResponseEntityBuilder.build(appointmentService.update(id, req));
	}

	/**
	 * Deletes an appointment.
	 */
	@PreAuthorize("@ownershipChecker.isAdmin(authentication) or @ownershipChecker.isAppointmentOwner(authentication, #id)")
	@DeleteMapping(Routes.BY_ID)
	public ResponseEntity<Response<Void>> delete(@PathVariable @NonNull UUID id) {
		return ResponseEntityBuilder.build(appointmentService.delete(id));
	}
}
