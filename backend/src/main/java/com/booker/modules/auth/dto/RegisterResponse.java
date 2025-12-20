package com.booker.modules.auth.dto;

import java.util.UUID;

public class RegisterResponse {
    public UUID id;
    public String email;
    public String firstName;
    public String lastName;
    public UUID role;

    public RegisterResponse(UUID id, String email, String firstName, String lastName, UUID role) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}