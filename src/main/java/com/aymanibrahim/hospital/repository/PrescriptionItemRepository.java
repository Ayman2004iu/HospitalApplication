package com.aymanibrahim.hospital.repository;

import com.aymanibrahim.hospital.entity.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long> {
}
