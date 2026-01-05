package com.booker.modules.role.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleUpsertRequest {

    @NotBlank
    public String roleName;
}
