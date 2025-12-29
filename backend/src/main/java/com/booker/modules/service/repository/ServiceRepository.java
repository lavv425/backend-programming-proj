package com.booker.modules.service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.modules.service.entity.Service;

public interface ServiceRepository extends JpaRepository<Service, UUID> {
    Optional<Service> findByServiceName(String name);
    Boolean existsByServiceName(String name);
    Service deleteByServiceName(String name);
    List<Service> findAllByProfessional(UUID professionalId);
    List<Service> findAllByProfessionalAndActive(UUID professionalId, Boolean active);
    List<Service> findAllByActive(Boolean active);
    UUID getProfessional();
    List<Service> findByDurationLowerThan(Integer duration);
    List<Service> findByPriceLowerThan(Double price);
    List<Service> findByDurationHigherThan(Integer duration);
    List<Service> findByPriceHigherThan(Double price);
}
