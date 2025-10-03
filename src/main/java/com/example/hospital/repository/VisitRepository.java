package com.example.hospital.repository;

import com.example.hospital.entity.Patient;
import com.example.hospital.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findAllByPatient(Patient patient );
}