package com.booker.modules.appointment.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.modules.appointment.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByServiceId(UUID id);
    List<Appointment> findByProfessionalId(UUID professionalId);
    List<Appointment> findByCustomerId(UUID customerId);
    Optional<Appointment> findByIdAndCustomerId(UUID appointmentId, UUID customerId);
    List<Appointment> findByStatus(String status);
    List<Appointment> findByStartTimeBetween(Instant start, Instant end);
    List<Appointment> findByProfessionalIdAndStartTimeBetween(UUID professionalId, Instant start, Instant end);
    List<Appointment> findByCustomerIdAndStartTimeBetween(UUID customerId, Instant start, Instant end);
    List<Appointment> findByServiceIdAndStartTimeBetween(UUID serviceId, Instant start, Instant end);
    List<Appointment> findByProfessionalIdAndStatus(UUID professionalId, String status);
    List<Appointment> findByCustomerIdAndStatus(UUID customerId, String status);
    List<Appointment> findByServiceIdAndStatus(UUID serviceId, String status);
    List<Appointment> orderByStartTimeAsc(Instant start);
    List<Appointment> orderByStartTimeDesc(Instant start);
    List<Appointment> orderByEndTimeAsc(Instant end);
    List<Appointment> orderByEndTimeDesc(Instant end);
}
