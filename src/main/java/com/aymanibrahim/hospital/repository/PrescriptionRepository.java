package com.aymanibrahim.hospital.repository;

import com.aymanibrahim.hospital.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}

