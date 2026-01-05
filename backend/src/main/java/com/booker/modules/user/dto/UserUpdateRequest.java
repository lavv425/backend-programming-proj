package com.booker.modules.user.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserUpdateRequest {

    @NotBlank
    public String email;

    @NotBlank
    public String firstName;

    @NotBlank
    public String lastName;

    @NotNull
    public UUID role;
}
