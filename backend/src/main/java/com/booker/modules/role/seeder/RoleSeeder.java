package com.booker.modules.role.seeder;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.booker.modules.role.repository.RoleRepository;
import com.booker.modules.enums.user.UserRole;
import com.booker.modules.role.entity.Role;

@Component
public class RoleSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (UserRole r : UserRole.values()) {

            if (!roleRepository.existsByRoleName(r.name())) {
                Role role = new Role(r.name());

                roleRepository.save(role);
            }
        }
    }
}