package com.booker.modules.role.service;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import com.booker.modules.role.dto.RoleDto;
import com.booker.modules.role.dto.RoleUpsertRequest;
import com.booker.modules.role.entity.Role;
import com.booker.modules.role.repository.RoleRepository;
import com.booker.utils.base.Response;

/**
 * Service that handles the business logic for role management.
 * Manages user roles and permissions within the booking system.
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Retrieves all roles from the database.
     *
     * @return a response containing the list of all roles
     */
    public Response<List<RoleDto>> list() {
        List<RoleDto> data = roleRepository.findAll().stream()
                .map(RoleService::toDto)
                .toList();
        return new Response<>(true, data, SuccessCodes.OK);
    }

    /**
     * Retrieves a specific role by its unique identifier.
     *
     * @param id the unique identifier of the role
     * @return a response containing the role if found, or an error if not found
     */
    public Response<RoleDto> getById(@NonNull UUID id) {
        return roleRepository.findById(id)
                .map(r -> new Response<>(true, toDto(r), SuccessCodes.OK))
                .orElseGet(() -> new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND));
    }

    /**
     * Creates a new role in the system.
     *
     * @param req the request containing the role name
     * @return a response containing the newly created role, or an error if the role already exists
     */
    public Response<RoleDto> create(RoleUpsertRequest req) {
        if (roleRepository.existsByRoleName(req.roleName.trim())) {
            return new Response<>(false, null, ErrorCodes.DUPLICATE_RESOURCE);
        }
        Role role = new Role(req.roleName.trim());
        Role saved = roleRepository.save(role);
        return new Response<>(true, toDto(saved), SuccessCodes.ROLE_CREATED);
    }

    /**
     * Updates an existing role with new information.
     *
     * @param id the unique identifier of the role to update
     * @param req the request containing the updated role name
     * @return a response containing the updated role, or an error if the role is not found
     */
    public Response<RoleDto> update(@NonNull UUID id, RoleUpsertRequest req) {
        Role role = roleRepository.findById(id).orElse(null);
        if (role == null) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }
        role.setRoleName(req.roleName.trim());
        Role saved = roleRepository.save(role);
        return new Response<>(true, toDto(saved), SuccessCodes.ROLE_UPDATED);
    }

    /**
     * Deletes a role from the system.
     *
     * @param id the unique identifier of the role to delete
     * @return a response indicating success or failure of the deletion
     */
    public Response<Void> delete(@NonNull UUID id) {
        if (!roleRepository.existsById(id)) {
            return new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND);
        }
        roleRepository.deleteById(id);
        return new Response<>(true, null, SuccessCodes.ROLE_DELETED);
    }

    private static RoleDto toDto(Role r) {
        return new RoleDto(r.getId(), r.getRoleName(), r.getCreatedAt());
    }
}
