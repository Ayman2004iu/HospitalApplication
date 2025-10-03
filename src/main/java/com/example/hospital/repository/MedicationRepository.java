package com.example.hospital.repository;

import com.example.hospital.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MedicationRepository extends JpaRepository<Medication, Long> {
}
