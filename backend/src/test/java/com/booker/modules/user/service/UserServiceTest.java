package com.booker.modules.user.service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.user.dto.UserPublicDto;
import com.booker.modules.user.dto.UserUpdateRequest;
import com.booker.modules.user.entity.User;
import com.booker.modules.user.repository.UserRepository;
import com.booker.services.MinioService;
import com.booker.utils.base.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private UserService userService;

    @Test
    void list_shouldReturnAllUsers() {
        User user1 = createTestUser("user1@test.com", "John", "Doe");
        User user2 = createTestUser("user2@test.com", "Jane", "Smith");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        Response<List<UserPublicDto>> response = userService.list();

        assertTrue(response.status);
        assertEquals(SuccessCodes.OK, response.message);
        assertEquals(2, response.data.size());
        verify(userRepository).findAll();
    }

    @Test
    void getById_whenUserExists_shouldReturnUser() {
        UUID userId = UUID.randomUUID();
        User user = createTestUser("test@example.com", "John", "Doe");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Response<UserPublicDto> response = userService.getById(userId);

        assertTrue(response.status);
        assertEquals(SuccessCodes.OK, response.message);
        assertNotNull(response.data);
        assertEquals("test@example.com", response.data.email);
        verify(userRepository).findById(userId);
    }

    @Test
    void getById_whenUserNotFound_shouldReturnError() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Response<UserPublicDto> response = userService.getById(userId);

        assertFalse(response.status);
        assertEquals(ErrorCodes.USER_NOT_FOUND, response.message);
        assertNull(response.data);
    }

    @Test
    void update_whenUserExists_shouldUpdateAndReturnUser() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        User user = createTestUser("old@example.com", "Old", "Name");
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.email = "new@example.com";
        request.firstName = "New";
        request.lastName = "Name";
        request.role = roleId;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Response<UserPublicDto> response = userService.update(userId, request);

        assertTrue(response.status);
        assertEquals(SuccessCodes.USER_UPDATED, response.message);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_whenUserNotFound_shouldReturnError() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest();
        request.email = "new@example.com";
        request.firstName = "New";
        request.lastName = "Name";
        request.role = roleId;
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Response<UserPublicDto> response = userService.update(userId, request);

        assertFalse(response.status);
        assertEquals(ErrorCodes.USER_NOT_FOUND, response.message);
        verify(userRepository, never()).save(any());
    }

    @Test
    void upsertProfileImage_whenUserNotFound_shouldReturnError() throws Exception {
        UUID userId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Response<UserPublicDto> response = userService.upsertProfileImage(userId, file);

        assertFalse(response.status);
        assertEquals(ErrorCodes.USER_NOT_FOUND, response.message);
        verify(minioService, never()).uploadFile(any(MultipartFile.class), any(UUID.class));
    }

    @Test
    void upsertProfileImage_whenInvalidFileType_shouldReturnError() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = createTestUser("test@example.com", "John", "Doe");
        MultipartFile file = mock(MultipartFile.class);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(file.getContentType()).thenReturn("application/pdf");

        Response<UserPublicDto> response = userService.upsertProfileImage(userId, file);

        assertFalse(response.status);
        assertEquals(ErrorCodes.INVALID_REQUEST_DATA, response.message);
        verify(minioService, never()).uploadFile(any(MultipartFile.class), any(UUID.class));
    }

    @Test
    void deleteProfileImage_whenUserNotFound_shouldReturnError() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Response<UserPublicDto> response = userService.deleteProfileImage(userId);

        assertFalse(response.status);
        assertEquals(ErrorCodes.USER_NOT_FOUND, response.message);
        verify(minioService, never()).deleteFile(anyString());
    }

    @Test
    void deleteProfileImage_whenUserHasNoImage_shouldSucceed() {
        UUID userId = UUID.randomUUID();
        User user = createTestUser("test@example.com", "John", "Doe");
        user.setProfileImageUrl(null);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Response<UserPublicDto> response = userService.deleteProfileImage(userId);

        assertTrue(response.status);
        assertEquals(SuccessCodes.PROFILE_IMAGE_DELETED, response.message);
        verify(minioService, never()).deleteFile(anyString());
    }

    @Test
    void delete_whenUserExists_shouldDeleteUser() {
        UUID userId = UUID.randomUUID();
        
        when(userRepository.existsById(userId)).thenReturn(true);

        Response<Void> response = userService.delete(userId);

        assertTrue(response.status);
        assertEquals(SuccessCodes.USER_DELETED, response.message);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void delete_whenUserNotFound_shouldReturnError() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        Response<Void> response = userService.delete(userId);

        assertFalse(response.status);
        assertEquals(ErrorCodes.USER_NOT_FOUND, response.message);
    }

    private User createTestUser(String email, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(UUID.randomUUID());
        return user;
    }
}
