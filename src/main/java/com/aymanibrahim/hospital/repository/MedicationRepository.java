package com.aymanibrahim.hospital.repository;

import com.aymanibrahim.hospital.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
}