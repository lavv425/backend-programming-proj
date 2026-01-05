package com.booker.modules.service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.modules.service.entity.Service;

public interface ServiceRepository extends JpaRepository<Service, UUID> {
    Optional<Service> findByName(String name);
    Boolean existsByName(String name);
    void deleteByName(String name);
    List<Service> findAllByProfessional(UUID professionalId);
    List<Service> findAllByProfessionalAndActive(UUID professionalId, Boolean active);
    List<Service> findAllByActive(Boolean active);
    List<Service> findByDurationInMinutesLessThan(Integer durationInMinutes);
    List<Service> findByDurationInMinutesGreaterThan(Integer durationInMinutes);
    List<Service> findByPriceLessThan(Double price);
    List<Service> findByPriceGreaterThan(Double price);
}
