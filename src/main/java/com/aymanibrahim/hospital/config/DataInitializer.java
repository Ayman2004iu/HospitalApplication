package com.aymanibrahim.hospital.config;

import com.aymanibrahim.hospital.entity.Role;
import com.aymanibrahim.hospital.entity.User;
import com.aymanibrahim.hospital.enums.RoleName;
import com.aymanibrahim.hospital.repository.RoleRepository;
import com.aymanibrahim.hospital.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    @PostConstruct
    public void init() {
        Arrays.stream(RoleName.values()).forEach(this::createRoleIfNotExists);

        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }

        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {
        if (userRepository.existsByEmail(adminEmail) || userRepository.existsByUsername(adminUsername)) {
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found after initialization"));

        User admin = User.builder()
                .email(adminEmail)
                .username(adminUsername)
                .fullName("System Administrator")
                .password(passwordEncoder.encode(adminPassword))
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);
    }

    private void createRoleIfNotExists(RoleName roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}