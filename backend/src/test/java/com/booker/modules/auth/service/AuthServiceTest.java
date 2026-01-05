package com.booker.modules.auth.service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.auth.dto.login.LoginRequest;
import com.booker.modules.auth.dto.login.LoginResponse;
import com.booker.modules.auth.dto.register.RegisterRequest;
import com.booker.modules.auth.dto.register.RegisterResponse;
import com.booker.modules.enums.user.UserRole;
import com.booker.modules.log.service.LoggerService;
import com.booker.modules.role.entity.Role;
import com.booker.modules.role.repository.RoleRepository;
import com.booker.modules.user.entity.User;
import com.booker.modules.user.repository.UserRepository;
import com.booker.security.jwt.JwtProperties;
import com.booker.security.service.TokenBlacklistService;
import com.booker.services.EmailService;
import com.booker.utils.base.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private EmailService emailService;

    @Mock
    private LoggerService loggerService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_whenEmailAlreadyExists_shouldReturnError() {
        RegisterRequest request = new RegisterRequest();
        request.email = "test@example.com";
        request.password = "password";
        request.firstName = "John";
        request.lastName = "Doe";
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        Response<RegisterResponse> response = authService.register(request);

        assertFalse(response.status);
        assertEquals(ErrorCodes.EMAIL_ALREADY_EXISTS, response.message);
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_whenRoleNotFound_shouldReturnServiceUnavailable() {
        RegisterRequest request = new RegisterRequest();
        request.email = "test@example.com";
        request.password = "password";
        request.firstName = "John";
        request.lastName = "Doe";
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByRoleName(UserRole.CUSTOMER.name())).thenReturn(Optional.empty());

        Response<RegisterResponse> response = authService.register(request);

        assertFalse(response.status);
        assertEquals(ErrorCodes.SERVICE_UNAVAILABLE, response.message);
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_whenValidRequest_shouldCreateUserAndSendEmail() {
        RegisterRequest request = new RegisterRequest();
        request.email = "test@example.com";
        request.password = "password";
        request.firstName = "John";
        request.lastName = "Doe";
        Role customerRole = new Role(UserRole.CUSTOMER.name());
        User savedUser = new User();
        savedUser.setEmail("test@example.com");
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setRole(customerRole.getId());

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByRoleName(UserRole.CUSTOMER.name())).thenReturn(Optional.of(customerRole));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Response<RegisterResponse> response = authService.register(request);

        assertTrue(response.status);
        assertEquals(SuccessCodes.USER_REGISTERED, response.message);
        assertNotNull(response.data);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendRegistrationConfirmation(anyString(), anyString());
        verify(loggerService).success(anyString(), eq("AuthService"));
    }

    @Test
    void login_whenUserNotFound_shouldReturnInvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.email = "test@example.com";
        request.password = "password";
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        Response<LoginResponse> response = authService.login(request);

        assertFalse(response.status);
        assertEquals(ErrorCodes.INVALID_CREDENTIALS, response.message);
    }

    @Test
    void login_whenInvalidPassword_shouldReturnInvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.email = "test@example.com";
        request.password = "wrongpassword";
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"); // "password"

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Response<LoginResponse> response = authService.login(request);

        assertFalse(response.status);
        assertEquals(ErrorCodes.INVALID_CREDENTIALS, response.message);
    }

    @Test
    void logout_whenValidAuthentication_shouldInvalidateToken() {
        Authentication authentication = mock(Authentication.class);

        authService.logout(authentication);

        verify(tokenBlacklistService).invalidateToken(authentication);
        verify(loggerService).success(anyString(), eq("AuthService"));
    }
}
