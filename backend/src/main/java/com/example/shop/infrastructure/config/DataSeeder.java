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

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Seed Roles
        if (roleRepository.count() == 0) {
            log.info("Seeding roles...");
            Role adminRole = Role.builder().name("ADMIN").build();
            Role customerRole = Role.builder().name("CUSTOMER").build();

            roleRepository.save(adminRole);
            roleRepository.save(customerRole);
        }

        // Seed Admin Account
        if (userRepository.count() == 0) {
            log.info("Seeding system admin account...");
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

            User admin = User.builder()
                    .username("admin")
                    .email("admin@shop.com")
                    .fullName("System Administrator")
                    .password(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .isActive(true)
                    .build();

            userRepository.save(admin);
        }
    }
}
