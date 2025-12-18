package com.booker.modules.dto.auth;

import com.booker.modules.enums.user.Role;
import java.util.UUID;

public class RegisterResponse {
    public UUID id;
    public String email;
    public String firstName;
    public String lastName;
    public Role role;

    public RegisterResponse(UUID id, String email, String firstName, String lastName, Role role) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}