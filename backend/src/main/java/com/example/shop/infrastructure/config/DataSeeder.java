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
            // Optional: Ensure the existing superadmin always has the most updated all_permissions
            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN").orElse(null);
            
            if (superAdminRole != null) {
                List<Permission> allPermissions = permissionRepository.findAll();
                superAdminRole.setPermissions(new HashSet<>(allPermissions));
                roleRepository.save(superAdminRole);
                log.info("Super Admin permissions updated with the latest permissions.");
            }
        }
    }
}
