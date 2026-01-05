package com.booker.modules.appointment.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.modules.appointment.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByService(UUID service);
    List<Appointment> findByProfessional(UUID professional);
    List<Appointment> findByCustomer(UUID customer);
    Optional<Appointment> findByIdAndCustomer(UUID appointmentId, UUID customer);
    List<Appointment> findByStatus(String status);
    List<Appointment> findByStartTimeBetween(Instant start, Instant end);
    List<Appointment> findByProfessionalAndStartTimeBetween(UUID professional, Instant start, Instant end);
    List<Appointment> findByCustomerAndStartTimeBetween(UUID customer, Instant start, Instant end);
    List<Appointment> findByServiceAndStartTimeBetween(UUID service, Instant start, Instant end);
    List<Appointment> findByProfessionalAndStatus(UUID professional, String status);
    List<Appointment> findByCustomerAndStatus(UUID customer, String status);
    List<Appointment> findByServiceAndStatus(UUID service, String status);
    List<Appointment> findAllByOrderByStartTimeAsc();
    List<Appointment> findAllByOrderByStartTimeDesc();
    List<Appointment> findAllByOrderByEndTimeAsc();
    List<Appointment> findAllByOrderByEndTimeDesc();
}
