package com.booker.modules.professional.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.modules.professional.entity.Professional;

public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {
    List<Professional> findByIsVerifiedTrue();
    List<Professional> findByAverageRatingGreaterThan(Integer average);
    List<Professional> findByYearsOfExperienceGreaterThanEqual(Integer years);
}