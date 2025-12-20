package com.booker.modules.role.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.modules.role.entity.Role;

public interface RoleRepository extends JpaRepository<Role, UUID>{
    Optional<Role> findByRoleName(String roleName);
    boolean existsByRoleName(String roleName);
    Optional<Role> findById(UUID id);
    boolean existsById(UUID id);
}
