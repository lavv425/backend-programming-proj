package com.booker.modules.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.modules.user.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}