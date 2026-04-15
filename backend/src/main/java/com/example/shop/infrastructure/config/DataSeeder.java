package com.example.shop.infrastructure.config;

import com.example.shop.domain.entity.Role;
import com.example.shop.domain.entity.User;
import com.example.shop.domain.repository.RoleRepository;
import com.example.shop.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import com.example.shop.domain.entity.Permission;
import com.example.shop.domain.repository.PermissionRepository;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking for Super Admin initialization...");

        if (!userRepository.existsByUsername("superadmin")) {
            log.info("Super Admin not found. Creating a new one...");

            // Get all current permissions in the system
            List<Permission> allPermissions = permissionRepository.findAll();
            
            if (allPermissions.isEmpty()) {
                log.warn("No permissions found in DB. Please ensure your migration scripts ran successfully.");
                return;
            }

            // Create or update SUPER_ADMIN role with all permissions
            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setName("SUPER_ADMIN");
                return role;
            });
            
            superAdminRole.setPermissions(new HashSet<>(allPermissions));
            roleRepository.save(superAdminRole);

            // Assign SUPER_ADMIN role to superadmin user
            Set<Role> roles = new HashSet<>();
            roles.add(superAdminRole);

            User superAdmin = User.builder()
                    .username("superadmin")
                    .fullName("System Owner")
                    .email("super@admin.system")
                    .password(passwordEncoder.encode("superadmin"))
                    .roles(roles)
                    .isActive(true)
                    .build();

            userRepository.save(superAdmin);
            log.info("Super Admin has been created successfully (username: superadmin, password: superadmin).");
        } else {
            Role adminRole = roleRepository.findByName("SUPER_ADMIN").orElse(null);

            if (adminRole != null) {
                List<Permission> allPermissions = permissionRepository.findAll();

                Set<Permission> currentPermissions = adminRole.getPermissions();
                Set<Permission> newPermissions = new HashSet<>(currentPermissions);

                for (Permission p : allPermissions) {
                    if (!currentPermissions.contains(p)) {
                        newPermissions.add(p);
                    }
                }

                // chỉ save nếu có thay đổi
                if (newPermissions.size() != currentPermissions.size()) {
                    adminRole.setPermissions(newPermissions);
                    roleRepository.save(adminRole);
                    log.info("Added missing permissions to SUPER_ADMIN.");
                } else {
                    log.info("SUPER_ADMIN permissions already up to date.");
                }
            }
        }
    }
}
