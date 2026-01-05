package com.booker.modules.role.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import com.booker.constants.routes.Namespaces;
import com.booker.constants.routes.Routes;
import com.booker.modules.role.dto.RoleDto;
import com.booker.modules.role.dto.RoleUpsertRequest;
import com.booker.modules.role.service.RoleService;
import com.booker.utils.base.Response;
import com.booker.utils.base.ResponseEntityBuilder;

import jakarta.validation.Valid;

/**
 * REST controller that handles role management operations.
 * Provides endpoints for creating, reading, updating, and deleting user roles.
 */
@RestController
@RequestMapping(Namespaces.ROLES)
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Retrieves a list of all roles in the system.
     *
     * @return a response entity containing the list of roles
     */
    @GetMapping(Routes.ROOT)
    public ResponseEntity<Response<List<RoleDto>>> list() {
        return ResponseEntityBuilder.build(roleService.list());
    }

    /**
     * Retrieves a specific role by its unique identifier.
     *
     * @param id the unique identifier of the role
     * @return a response entity containing the requested role
     */
    @GetMapping(Routes.BY_ID)
    public ResponseEntity<Response<RoleDto>> getById(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(roleService.getById(id));
    }

    /**
     * Creates a new role with the provided information.
     *
     * @param req the request containing the role details
     * @return a response entity containing the newly created role
     */
    @PostMapping(Routes.ROOT)
    public ResponseEntity<Response<RoleDto>> create(@Valid @RequestBody RoleUpsertRequest req) {
        return ResponseEntityBuilder.build(roleService.create(req));
    }

    /**
     * Updates an existing role with new information.
     *
     * @param id the unique identifier of the role to update
     * @param req the request containing the updated role details
     * @return a response entity containing the updated role
     */
    @PutMapping(Routes.BY_ID)
    public ResponseEntity<Response<RoleDto>> update(@PathVariable @NonNull UUID id, @Valid @RequestBody RoleUpsertRequest req) {
        return ResponseEntityBuilder.build(roleService.update(id, req));
    }

    /**
     * Deletes a role from the system.
     *
     * @param id the unique identifier of the role to delete
     * @return a response entity indicating the result of the deletion
     */
    @DeleteMapping(Routes.BY_ID)
    public ResponseEntity<Response<Void>> delete(@PathVariable @NonNull UUID id) {
        return ResponseEntityBuilder.build(roleService.delete(id));
    }
}
