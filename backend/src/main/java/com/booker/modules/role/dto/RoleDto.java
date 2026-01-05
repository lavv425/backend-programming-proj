package com.booker.modules.role.dto;

import java.time.Instant;
import java.util.UUID;

public class RoleDto {
    public UUID id;
    public String roleName;
    public Instant createdAt;

    public RoleDto(UUID id, String roleName, Instant createdAt) {
        this.id = id;
        this.roleName = roleName;
        this.createdAt = createdAt;
    }
}
