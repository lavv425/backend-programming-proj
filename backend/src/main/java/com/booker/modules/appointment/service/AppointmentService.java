package com.booker.modules.appointment.service;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.appointment.dto.AppointmentDto;
import com.booker.modules.appointment.dto.AppointmentUpsertRequest;
import com.booker.modules.appointment.entity.Appointment;
import com.booker.modules.appointment.repository.AppointmentRepository;
import com.booker.modules.customer.repository.CustomerRepository;
import com.booker.modules.customer.entity.Customer;
import com.booker.services.EmailService;
import com.booker.modules.log.service.LoggerService;
import com.booker.utils.base.Response;

/**
 * Manages appointment scheduling and lifecycle operations.
 * 
 * This service handles all appointment-related operations including creating,
 * updating, and canceling appointments. It sends email notifications to customers
 * for appointment confirmations and cancellations.
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final LoggerService loggerService;

    public AppointmentService(AppointmentRepository appointmentRepository, CustomerRepository customerRepository, EmailService emailService, LoggerService loggerService) {
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.loggerService = loggerService;
    }

    /**
     * Retrieves all appointments in the system.
     * 
     * @return a response containing a list of all appointments
     */
    public Response<List<AppointmentDto>> list() {
        List<AppointmentDto> data = appointmentRepository.findAll().stream()
                .map(AppointmentService::toDto)
                .toList();
        return new Response<>(true, data, SuccessCodes.OK);
    }

    /**
     * Retrieves a specific appointment by its unique identifier.
     * 
     * @param id the unique identifier of the appointment
     * @return a response containing the appointment details or an error if not found
     */
    public Response<AppointmentDto> getById(@NonNull UUID id) {
        return appointmentRepository.findById(id)
                .map(a -> new Response<>(true, toDto(a), SuccessCodes.OK))
                .orElseGet(() -> new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND));
    }

    /**
     * Creates a new appointment booking.
     * 
     * Schedules an appointment with the specified customer, professional, and service.
     * Sends a confirmation email to the customer with the appointment details including
     * the professional name, service, and scheduled time.
     * 
     * @param req the appointment creation request containing time, customer, professional, and service details
     * @return a response containing the created appointment data
     */
    public Response<AppointmentDto> create(AppointmentUpsertRequest req) {
        Appointment appointment = new Appointment();
        appointment.setStartTime(req.startTime);
        appointment.setEndTime(req.endTime);
        appointment.setStatus(req.status.trim());
        appointment.setCustomer(req.customer);
        appointment.setProfessional(req.professional);
        appointment.setService(req.service);

        Appointment saved = appointmentRepository.save(appointment);
        
        loggerService.success("Appointment created: " + saved.getId(), "AppointmentService");
        
        // confirmation email
        try {
            Customer customer = customerRepository.findById(saved.getCustomer()).orElse(null);
            if (customer != null && customer.getEmail() != null) {
                LocalDateTime startDateTime = LocalDateTime.ofInstant(saved.getStartTime(), ZoneId.systemDefault());
                emailService.sendAppointmentConfirmation(
                    customer.getEmail(),
                    customer.getFirstName() + " " + customer.getLastName(),
                    "Professionista",
                    "Servizio",
                    startDateTime
                );
            }
        } catch (Exception e) {
            loggerService.error("Failed to send appointment confirmation email: " + e.getMessage(), "AppointmentService");
        }
        
        return new Response<>(true, toDto(saved), SuccessCodes.APPOINTMENT_BOOKED);
    }

    /**
     * Updates an existing appointment.
     * 
     * Allows modification of appointment time, status, and associated customer,
     * professional, or service. No email notification is sent for updates.
     * 
     * @param id the unique identifier of the appointment to update
     * @param req the update request containing the new appointment data
     * @return a response containing the updated appointment data or an error if not found
     */
    public Response<AppointmentDto> update(@NonNull UUID id, AppointmentUpsertRequest req) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }

        appointment.setStartTime(req.startTime);
        appointment.setEndTime(req.endTime);
        appointment.setStatus(req.status.trim());
        appointment.setCustomer(req.customer);
        appointment.setProfessional(req.professional);
        appointment.setService(req.service);

        Appointment saved = appointmentRepository.save(appointment);
        return new Response<>(true, toDto(saved), SuccessCodes.APPOINTMENT_UPDATED);
    }

    /**
     * Cancels and deletes an appointment.
     * 
     * Sends a cancellation email to the customer with the appointment details
     * before permanently removing the appointment from the database.
     * 
     * @param id the unique identifier of the appointment to delete
     * @return a response indicating success or an error if the appointment is not found
     */
    public Response<Void> delete(@NonNull UUID id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }
        
        try {
            Customer customer = customerRepository.findById(appointment.getCustomer()).orElse(null);
            if (customer != null && customer.getEmail() != null) {
                LocalDateTime startDateTime = LocalDateTime.ofInstant(appointment.getStartTime(), ZoneId.systemDefault());
                emailService.sendAppointmentCancellation(
                    customer.getEmail(),
                    customer.getFirstName() + " " + customer.getLastName(),
                    "Servizio",
                    startDateTime
                );
            }
        } catch (Exception e) {
            loggerService.error("Failed to send appointment cancellation email: " + e.getMessage(), "AppointmentService");
        }
        
        appointmentRepository.deleteById(id);
        loggerService.success("Appointment cancelled: " + id, "AppointmentService");
        return new Response<>(true, null, SuccessCodes.APPOINTMENT_CANCELLED);
    }

    private static AppointmentDto toDto(Appointment a) {
        return new AppointmentDto(
                a.getId(),
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus(),
                a.getCustomer(),
                a.getProfessional(),
                a.getService(),
                a.getCreatedAt());
    }
}
