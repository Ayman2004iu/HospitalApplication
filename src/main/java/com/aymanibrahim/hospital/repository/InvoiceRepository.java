package com.aymanibrahim.hospital.repository;

import com.aymanibrahim.hospital.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByVisit_Id(Long visitId);

    @EntityGraph(attributePaths = {"lines"})
    Page<Invoice> findAll(Pageable pageable);
}