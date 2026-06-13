package com.aymanibrahim.hospital.repository;

import com.aymanibrahim.hospital.entity.Role;
import com.aymanibrahim.hospital.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
    boolean existsByName(RoleName name);
}