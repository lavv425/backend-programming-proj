package com.booker.modules.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.modules.entities.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}