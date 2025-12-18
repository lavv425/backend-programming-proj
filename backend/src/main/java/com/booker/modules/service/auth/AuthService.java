package com.booker.modules.service.auth;

import com.booker.constants.ErrorCodes;
import com.booker.modules.dto.auth.RegisterRequest;
import com.booker.modules.dto.auth.RegisterResponse;
import com.booker.modules.entities.user.User;
import com.booker.modules.enums.user.Role;
import com.booker.modules.repository.user.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email)) {
            // TODO: Create error handler
            throw new IllegalArgumentException(ErrorCodes.EMAIL_ALREADY_EXISTS);
        }

        User u = new User();
        u.setEmail(req.email.toLowerCase().trim());
        u.setPasswordHash(encoder.encode(req.password));
        u.setFirstName(req.firstName.trim());
        u.setLastName(req.lastName.trim());
        u.setRole(Role.CUSTOMER);

        User saved = userRepository.save(u);

        return new RegisterResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getRole()
        );
    }
}