package com.booker.modules.role.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(name = "uk_roles_role_name", columnNames = "role_name"), indexes = @Index(name = "idx_roles_role_name", columnList = "role_name"))
public class Role {

    public Role(String roleName) {
        this.roleName = roleName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "role_name", length = 50, nullable = false)
    private String roleName;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    // getters/setters
    public UUID getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
