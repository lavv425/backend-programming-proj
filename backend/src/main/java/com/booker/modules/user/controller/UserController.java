package com.booker.modules.user.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.booker.constants.routes.Namespaces;
import com.booker.constants.routes.Routes;
import com.booker.modules.user.dto.UserPublicDto;
import com.booker.modules.user.dto.UserUpdateRequest;
import com.booker.modules.user.service.UserService;
import com.booker.utils.base.Response;
import com.booker.utils.base.ResponseEntityBuilder;

import jakarta.validation.Valid;

/**
 * Manages user profiles and information.
 * Provides endpoints to list, view, update, and delete users, as well as manage profile images.
 */
@RestController
@RequestMapping(Namespaces.USERS)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets all users in the system.
     */
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping(Routes.ROOT)
    public ResponseEntity<Response<List<UserPublicDto>>> list() {
        return ResponseEntityBuilder.build(userService.list());
    }

    /**
     * Gets a specific user by their ID.
     */
    @GetMapping(Routes.BY_ID)
    public ResponseEntity<Response<UserPublicDto>> getById(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(userService.getById(id));
    }

    /**
     * Updates a user's information.
     */
    @PreAuthorize("@ownershipChecker.isAdminOrOwner(authentication, #id)")
    @PutMapping(Routes.BY_ID)
    public ResponseEntity<Response<UserPublicDto>> update(@PathVariable @NonNull UUID id, @Valid @RequestBody UserUpdateRequest req) {
        return ResponseEntityBuilder.build(userService.update(id, req));
    }

    /**
     * Uploads or updates a user's profile image.
     */
    @PreAuthorize("@ownershipChecker.isAdminOrOwner(authentication, #id)")
    @PutMapping(value = Routes.BY_ID + Routes.PROFILE_IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<UserPublicDto>> upsertProfileImage(
            @PathVariable @NonNull UUID id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntityBuilder.build(userService.upsertProfileImage(id, file));
    }

    /**
     * Removes a user's profile image.
     */
    @PreAuthorize("@ownershipChecker.isAdminOrOwner(authentication, #id)")
    @DeleteMapping(Routes.BY_ID + Routes.PROFILE_IMAGE)
    public ResponseEntity<Response<UserPublicDto>> deleteProfileImage(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(userService.deleteProfileImage(id));
    }

    /**
     * Deletes a user from the system.
     */
    @DeleteMapping(Routes.BY_ID)
    public ResponseEntity<Response<Void>> delete(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(userService.delete(id));
    }
}
