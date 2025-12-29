package com.booker.modules.auth.service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.utils.base.Response;
import com.booker.modules.auth.dto.RegisterRequest;
import com.booker.modules.auth.dto.RegisterResponse;
import com.booker.modules.enums.user.UserRole;
import com.booker.modules.role.entity.Role;
import com.booker.modules.role.repository.RoleRepository;
import com.booker.modules.user.entity.User;
import com.booker.modules.user.repository.UserRepository;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
                saved.getRole()
            );

        return new Response<RegisterResponse>(true, responseDto, SuccessCodes.USER_REGISTERED);
    }
}