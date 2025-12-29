package com.booker.modules.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.booker.constants.routes.Namespaces;
import com.booker.constants.routes.Routes;
import com.booker.modules.auth.dto.login.LoginRequest;
import com.booker.modules.auth.dto.register.RegisterRequest;
import com.booker.modules.auth.dto.register.RegisterResponse;
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

    @PostMapping(Routes.LOGIN)
    @ResponseStatus(HttpStatus.OK)
    public Response<?> login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}