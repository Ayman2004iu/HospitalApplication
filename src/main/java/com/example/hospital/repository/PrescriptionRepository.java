package com.example.hospital.repository;

import com.example.hospital.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}

