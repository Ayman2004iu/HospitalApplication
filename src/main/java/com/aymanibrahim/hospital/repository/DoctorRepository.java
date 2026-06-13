package com.aymanibrahim.hospital.repository;

import com.aymanibrahim.hospital.entity.Doctor;
import com.aymanibrahim.hospital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);
}