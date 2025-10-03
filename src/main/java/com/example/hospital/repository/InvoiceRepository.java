package com.example.hospital.repository;

import com.example.hospital.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByVisit_Id(Long visitId);
}

