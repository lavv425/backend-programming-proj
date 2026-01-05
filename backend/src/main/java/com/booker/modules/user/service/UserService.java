package com.booker.modules.user.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.user.dto.UserPublicDto;
import com.booker.modules.user.dto.UserUpdateRequest;
import com.booker.modules.user.entity.User;
import com.booker.modules.user.repository.UserRepository;
import com.booker.services.MinioService;
import com.booker.utils.base.Response;

/**
 * Manages user profile operations and data.
 * 
 * This service handles all user-related operations including listing users,
 * retrieving user details, updating profiles, and managing profile images.
 * Profile images are stored in MinIO object storage.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final MinioService minioService;

    public UserService(UserRepository userRepository, MinioService minioService) {
        this.userRepository = userRepository;
        this.minioService = minioService;
    }

    /**
     * Retrieves all users in the system.
     * 
     * @return a response containing a list of all users with their public profile data
     */
    public Response<List<UserPublicDto>> list() {
        List<UserPublicDto> data = userRepository.findAll().stream()
                .map(UserService::toDto)
                .toList();
        return new Response<>(true, data, SuccessCodes.OK);
    }

    /**
     * Retrieves a specific user by their unique identifier.
     * 
     * @param id the unique identifier of the user to retrieve
     * @return a response containing the user's public profile data or an error if not found
     */
    public Response<UserPublicDto> getById(@NonNull UUID id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new Response<>(false, null, ErrorCodes.USER_NOT_FOUND);
        }
        return new Response<>(true, toDto(user), SuccessCodes.OK);
    }

    /**
     * Updates a user's profile information.
     * 
     * Allows modification of email, first name, last name, and role. The email
     * is automatically normalized to lowercase and trimmed.
     * 
     * @param id the unique identifier of the user to update
     * @param req the update request containing the new user data
     * @return a response containing the updated user data or an error if the user is not found
     */
    public Response<UserPublicDto> update(@NonNull UUID id, UserUpdateRequest req) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new Response<>(false, null, ErrorCodes.USER_NOT_FOUND);
        }

        user.setEmail(req.email.toLowerCase().trim());
        user.setFirstName(req.firstName.trim());
        user.setLastName(req.lastName.trim());
        user.setRole(req.role);

        User saved = userRepository.save(user);
        return new Response<>(true, toDto(saved), SuccessCodes.USER_UPDATED);
    }

    /**
     * Uploads or replaces a user's profile image.
     * 
     * If the user already has a profile image, the old one is deleted from MinIO
     * before uploading the new one. The image is stored in MinIO object storage
     * and the user's profile is updated with the new public URL.
     * 
     * @param id the unique identifier of the user
     * @param file the image file to upload
     * @return a response containing the updated user data with the new image URL or an error
     */
    public Response<UserPublicDto> upsertProfileImage(@NonNull UUID id, MultipartFile file) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new Response<>(false, null, ErrorCodes.USER_NOT_FOUND);
        }

        if (file == null || file.isEmpty()) {
            return new Response<>(false, null, ErrorCodes.INVALID_REQUEST_DATA);
        }

        // Elimina vecchio avatar se presente
        if (user.getProfileImageUrl() != null) {
            String oldObjectName = minioService.extractObjectName(user.getProfileImageUrl());
            if (oldObjectName != null) {
                minioService.deleteFile(oldObjectName);
            }
        }

        // Upload nuovo avatar
        try {
            String publicUrl = minioService.uploadFile(file, user.getId());
            user.setProfileImageUrl(publicUrl);
            User saved = userRepository.save(user);
            return new Response<>(true, toDto(saved), SuccessCodes.PROFILE_IMAGE_UPDATED);
        } catch (IOException e) {
            return new Response<>(false, null, ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes a user's profile image.
     * 
     * Deletes the profile image from MinIO storage and clears the image URL
     * from the user's profile.
     * 
     * @param id the unique identifier of the user
     * @return a response containing the updated user data without the profile image or an error
     */
    public Response<UserPublicDto> deleteProfileImage(@NonNull UUID id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new Response<>(false, null, ErrorCodes.USER_NOT_FOUND);
        }

        // Elimina file da MinIO se presente
        if (user.getProfileImageUrl() != null) {
            String objectName = minioService.extractObjectName(user.getProfileImageUrl());
            if (objectName != null) {
                minioService.deleteFile(objectName);
            }
        }

        user.setProfileImageUrl(null);
        User saved = userRepository.save(user);
        return new Response<>(true, toDto(saved), SuccessCodes.PROFILE_IMAGE_DELETED);
    }

    /**
     * Permanently deletes a user from the system.
     * 
     * @param id the unique identifier of the user to delete
     * @return a response indicating success or an error if the user is not found
     */
    public Response<Void> delete(@NonNull UUID id) {
        if (!userRepository.existsById(id)) {
            return new Response<>(false, null, ErrorCodes.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
        return new Response<>(true, null, SuccessCodes.USER_DELETED);
    }

    private static UserPublicDto toDto(User u) {
        return new UserPublicDto(
                u.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getRole(),
                u.getProfileImageUrl(),
                u.getCreatedAt()
        );
    }
}
