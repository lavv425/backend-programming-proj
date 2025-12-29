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

import java.time.Instant;
import java.util.Optional;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            JwtEncoder jwtEncoder,
            JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public Response<RegisterResponse> register(RegisterRequest req) {
        System.err.println("Registering user with email: " + req.email);
        if (userRepository.existsByEmail(req.email)) {
            // TODO: Create error handler
            throw new IllegalArgumentException(ErrorCodes.EMAIL_ALREADY_EXISTS);
        }

        Optional<Role> userRole = roleRepository.findByRoleName(UserRole.CUSTOMER.name());

        if (userRole.isEmpty()) {
            // we throw a "generic" exception if the role is not found
            throw new IllegalStateException(ErrorCodes.SERVICE_UNAVAILABLE);
        }

        User u = new User();
        u.setEmail(req.email.toLowerCase().trim());
        u.setPasswordHash(encoder.encode(req.password));
        u.setFirstName(req.firstName.trim());
        u.setLastName(req.lastName.trim());
        u.setRole(userRole.get().getId());

        User saved = userRepository.save(u);

        RegisterResponse responseDto = new RegisterResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getRole());

        return new Response<RegisterResponse>(true, responseDto, SuccessCodes.USER_REGISTERED);
    }

    public Response<LoginResponse> login(LoginRequest req) {
        Optional<User> userOpt = userRepository.findByEmail(req.email.toLowerCase().trim());

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException(ErrorCodes.INVALID_CREDENTIALS);
        }

        User user = userOpt.get();

        if (!encoder.matches(req.password, user.getPasswordHash())) {
            throw new IllegalArgumentException(ErrorCodes.INVALID_CREDENTIALS);
        }

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(jwtProperties.expirationSeconds()))
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("roleId", user.getRole())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        LoginResponse responseDto = new LoginResponse(token);

        return new Response<LoginResponse>(true, responseDto, SuccessCodes.USER_LOGGED_IN);
    }

}