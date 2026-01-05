package com.booker.modules.auth.service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.utils.base.Response;
import com.booker.modules.auth.dto.login.LoginRequest;
import com.booker.modules.auth.dto.login.LoginResponse;
import com.booker.modules.auth.dto.register.RegisterRequest;
import com.booker.modules.auth.dto.register.RegisterResponse;
import com.booker.modules.enums.user.UserRole;
import com.booker.modules.role.entity.Role;
import com.booker.modules.role.repository.RoleRepository;
import com.booker.security.jwt.JwtProperties;
import com.booker.modules.user.entity.User;
import com.booker.modules.user.repository.UserRepository;
import com.booker.services.EmailService;
import com.booker.modules.log.service.LoggerService;

import java.time.Instant;
import java.util.Optional;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles user authentication and registration operations.
 * 
 * This service manages the core authentication flow including user registration,
 * login with JWT token generation, and password hashing. It integrates with the
 * email service to send confirmation emails upon successful registration.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final EmailService emailService;
    private final LoggerService loggerService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            JwtEncoder jwtEncoder,
            JwtProperties jwtProperties,
            EmailService emailService,
            LoggerService loggerService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
        this.emailService = emailService;
        this.loggerService = loggerService;
    }

    /**
     * Registers a new user in the system.
     * 
     * Creates a new customer account with the provided information, hashing the password
     * securely. Upon successful registration, sends a confirmation email to the user.
     * The user is automatically assigned the CUSTOMER role.
     * 
     * @param req the registration request containing email, password, and user details
     * @return a response containing the newly created user data or an error code if registration fails
     */
    public Response<RegisterResponse> register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email.toLowerCase().trim())) {
            return new Response<>(false, null, ErrorCodes.EMAIL_ALREADY_EXISTS);
        }

        Optional<Role> userRole = roleRepository.findByRoleName(UserRole.CUSTOMER.name());

        if (userRole.isEmpty()) {
            return new Response<>(false, null, ErrorCodes.SERVICE_UNAVAILABLE);
        }

        try {
            User u = new User();
            u.setEmail(req.email.toLowerCase().trim());
            u.setPasswordHash(encoder.encode(req.password));
            u.setFirstName(req.firstName.trim());
            u.setLastName(req.lastName.trim());
            u.setRole(userRole.get().getId());

            User saved = userRepository.save(u);

            loggerService.success("User registered successfully: " + saved.getEmail(), "AuthService");

            try {
                emailService.sendRegistrationConfirmation(saved.getEmail(), saved.getFirstName());
            } catch (Exception emailEx) {
                // Log email sending failure but do not fail the registration
                loggerService.error("Failed to send registration email to " + saved.getEmail() + ": " + emailEx.getMessage(), "AuthService");
            }

            RegisterResponse responseDto = new RegisterResponse(
                    saved.getId(),
                    saved.getEmail(),
                    saved.getFirstName(),
                    saved.getLastName(),
                    saved.getRole());

            return new Response<>(true, responseDto, SuccessCodes.USER_REGISTERED);
        } catch (Exception e) {
            loggerService.error("User registration failed: " + e.getMessage(), "AuthService");
            return new Response<>(false, null, ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Authenticates a user and generates a JWT access token.
     * 
     * Validates the user's credentials by checking the email and comparing the
     * provided password against the stored hash. If authentication succeeds,
     * generates a JWT token containing the user's ID, email, and role.
     * 
     * @param req the login request containing email and password
     * @return a response containing the JWT token or an error code if authentication fails
     */
    public Response<LoginResponse> login(LoginRequest req) {
        Optional<User> userOpt = userRepository.findByEmail(req.email.toLowerCase().trim());

        if (userOpt.isEmpty()) {
            return new Response<>(false, null, ErrorCodes.INVALID_CREDENTIALS);
        }

        User user = userOpt.get();

        if (!encoder.matches(req.password, user.getPasswordHash())) {
            return new Response<>(false, null, ErrorCodes.INVALID_CREDENTIALS);
        }

        try {
            // Get user role name for scope
            Optional<Role> roleOpt = roleRepository.findById(user.getRole());
            String roleName = roleOpt.map(Role::getRoleName).orElse(UserRole.CUSTOMER.name());

            Instant now = Instant.now();

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer(jwtProperties.issuer())
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(jwtProperties.expirationSeconds()))
                    .subject(String.valueOf(user.getId()))
                    .claim("email", user.getEmail())
                    .claim("roleId", user.getRole())
                    .claim("scope", roleName) // Add roleNsme as scope for authorization
                    .build();

            JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
            String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

            loggerService.success("User logged in: " + user.getEmail(), "AuthService");

            LoginResponse responseDto = new LoginResponse(token);
            return new Response<>(true, responseDto, SuccessCodes.USER_LOGGED_IN);
        } catch (Exception e) {
            loggerService.error("User login failed: " + e.getMessage(), "AuthService");
            return new Response<>(false, null, ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

}