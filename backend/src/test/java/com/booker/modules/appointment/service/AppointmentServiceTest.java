package com.booker.modules.appointment.service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.appointment.dto.AppointmentDto;
import com.booker.modules.appointment.dto.AppointmentUpsertRequest;
import com.booker.modules.appointment.entity.Appointment;
import com.booker.modules.appointment.repository.AppointmentRepository;
import com.booker.modules.customer.entity.Customer;
import com.booker.modules.customer.repository.CustomerRepository;
import com.booker.modules.log.service.LoggerService;
import com.booker.services.EmailService;
import com.booker.utils.base.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private LoggerService loggerService;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void list_shouldReturnAllAppointments() {
        Appointment apt1 = createTestAppointment();
        Appointment apt2 = createTestAppointment();
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(apt1, apt2));

        Response<List<AppointmentDto>> response = appointmentService.list();

        assertTrue(response.status);
        assertEquals(SuccessCodes.OK, response.message);
        assertEquals(2, response.data.size());
        verify(appointmentRepository).findAll();
    }

    @Test
    void getById_whenAppointmentExists_shouldReturnAppointment() {
        UUID appointmentId = UUID.randomUUID();
        Appointment appointment = createTestAppointment();
        appointment.setCustomer(UUID.randomUUID());
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        Response<AppointmentDto> response = appointmentService.getById(appointmentId);

        assertTrue(response.status);
        assertEquals(SuccessCodes.OK, response.message);
        assertNotNull(response.data);
        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    void getById_whenAppointmentNotFound_shouldReturnError() {
        UUID appointmentId = UUID.randomUUID();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        Response<AppointmentDto> response = appointmentService.getById(appointmentId);

        assertFalse(response.status);
        assertEquals(ErrorCodes.RESOURCE_NOT_FOUND, response.message);
        assertNull(response.data);
    }

    @Test
    void create_shouldCreateAppointmentAndSendEmail() {
        AppointmentUpsertRequest request = new AppointmentUpsertRequest();
        request.startTime = Instant.now().plusSeconds(86400);
        request.endTime = Instant.now().plusSeconds(90000);
        request.status = "CONFIRMED";
        request.customer = UUID.randomUUID();
        request.professional = UUID.randomUUID();
        request.service = UUID.randomUUID();
        
        Customer customer = new Customer();
        customer.setEmail("customer@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        
        Appointment savedAppointment = createTestAppointment();
        savedAppointment.setCustomer(request.customer);
        
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);
        when(customerRepository.findById(request.customer)).thenReturn(Optional.of(customer));

        Response<AppointmentDto> response = appointmentService.create(request);

        assertTrue(response.status);
        assertEquals(SuccessCodes.APPOINTMENT_BOOKED, response.message);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(customerRepository).findById(request.customer);
        verify(emailService).sendAppointmentConfirmation(anyString(), anyString(), anyString(), anyString(), any());
        verify(loggerService).success(anyString(), anyString());
    }

    @Test
    void update_whenAppointmentNotFound_shouldReturnError() {
        UUID appointmentId = UUID.randomUUID();
        AppointmentUpsertRequest request = new AppointmentUpsertRequest();
        request.startTime = Instant.now().plusSeconds(86400);
        request.endTime = Instant.now().plusSeconds(90000);
        request.status = "CONFIRMED";
        request.customer = UUID.randomUUID();
        request.professional = UUID.randomUUID();
        request.service = UUID.randomUUID();
        
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        Response<AppointmentDto> response = appointmentService.update(appointmentId, request);

        assertFalse(response.status);
        assertEquals(ErrorCodes.RESOURCE_NOT_FOUND, response.message);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void delete_whenAppointmentExists_shouldCancelAndSendEmail() {
        UUID appointmentId = UUID.randomUUID();
        Appointment appointment = createTestAppointment();
        
        Customer customer = new Customer();
        customer.setEmail("customer@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(customerRepository.findById(appointment.getCustomer())).thenReturn(Optional.of(customer));

        Response<Void> response = appointmentService.delete(appointmentId);

        assertTrue(response.status);
        assertEquals(SuccessCodes.APPOINTMENT_CANCELLED, response.message);
        verify(appointmentRepository).deleteById(appointmentId);
        verify(emailService).sendAppointmentCancellation(anyString(), anyString(), anyString(), any());
        verify(loggerService).success(anyString(), anyString());
    }

    @Test
    void delete_whenAppointmentNotFound_shouldReturnError() {
        UUID appointmentId = UUID.randomUUID();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        Response<Void> response = appointmentService.delete(appointmentId);

        assertFalse(response.status);
        assertEquals(ErrorCodes.RESOURCE_NOT_FOUND, response.message);
    }

    private Appointment createTestAppointment() {
        Appointment appointment = new Appointment();
        appointment.setStartTime(Instant.now().plusSeconds(86400));
        appointment.setEndTime(Instant.now().plusSeconds(90000));
        appointment.setStatus("CONFIRMED");
        appointment.setCustomer(UUID.randomUUID());
        appointment.setProfessional(UUID.randomUUID());
        appointment.setService(UUID.randomUUID());
        return appointment;
    }
}
