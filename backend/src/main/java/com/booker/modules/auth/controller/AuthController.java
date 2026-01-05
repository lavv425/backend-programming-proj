package com.booker.modules.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.booker.constants.routes.Namespaces;
import com.booker.constants.routes.Routes;
import com.booker.modules.auth.dto.login.LoginRequest;
import com.booker.modules.auth.dto.login.LoginResponse;
import com.booker.modules.auth.dto.register.RegisterRequest;
import com.booker.modules.auth.dto.register.RegisterResponse;
import com.booker.modules.auth.service.AuthService;
import com.booker.utils.base.Response;
import com.booker.utils.base.ResponseEntityBuilder;

/**
 * Handles user authentication.
 * Provides endpoints for user registration and login.
 */
@RestController
@RequestMapping(Namespaces.AUTH)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account.
     */
    @PostMapping(Routes.REGISTER)
    public ResponseEntity<Response<RegisterResponse>> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntityBuilder.build(authService.register(req));
    }

    /**
     * Authenticates a user and returns a login token.
     */
    @PostMapping(Routes.LOGIN)
    public ResponseEntity<Response<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntityBuilder.build(authService.login(req));
    }

    /**
     * Logs out the current user.
     * In a stateless JWT system, the client should discard the token.
     * This endpoint confirms the logout action and invalidates the token server-side.
     */
    @PostMapping(Routes.LOGOUT)
    public ResponseEntity<Response<Void>> logout(Authentication authentication) {
        return ResponseEntityBuilder.build(authService.logout(authentication));
    }
}