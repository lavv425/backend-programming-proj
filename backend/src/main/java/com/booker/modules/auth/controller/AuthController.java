package com.booker.modules.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.booker.constants.routes.Namespaces;
import com.booker.constants.routes.Routes;
import com.booker.modules.auth.dto.RegisterRequest;
import com.booker.modules.auth.dto.RegisterResponse;
import com.booker.modules.auth.service.AuthService;
import com.booker.utils.base.Response;

@RestController
@RequestMapping(Namespaces.AUTH)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(Routes.REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    public Response<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }
}