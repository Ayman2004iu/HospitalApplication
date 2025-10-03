package com.example.hospital.config;

import com.example.hospital.entity.Role;
import com.example.hospital.entity.User;
import com.example.hospital.repository.RoleRepository;
import com.example.hospital.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        createRoleIfNotExists("ROLE_ADMIN");
        createRoleIfNotExists("ROLE_DOCTOR");
        createRoleIfNotExists("ROLE_PATIENT");
        createRoleIfNotExists("ROLE_PHARMACIST");
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Admin User")
                    .roles(new HashSet<>(Set.of(roleRepository.findByName("ROLE_ADMIN").get())))
                    .build();
            userRepository.save(admin);
        }
    }


    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        });
    }
}
