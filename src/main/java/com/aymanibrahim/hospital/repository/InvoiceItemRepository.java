package com.aymanibrahim.hospital.repository;

import com.aymanibrahim.hospital.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}