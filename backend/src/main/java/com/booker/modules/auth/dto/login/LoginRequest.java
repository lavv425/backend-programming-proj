package com.booker.modules.auth.dto.login;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    
    @NotBlank
    public String email;

    @NotBlank
    public String password;
}
