package com.aymanibrahim.hospital.repository;

import com.aymanibrahim.hospital.entity.Patient;
import com.aymanibrahim.hospital.entity.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("SELECT v FROM Visit v JOIN FETCH v.doctor JOIN FETCH v.clinic JOIN FETCH v.department WHERE v.patient = :patient")
    List<Visit> findAllByPatient(@Param("patient") Patient patient);

    @Query(value = "SELECT v FROM Visit v JOIN FETCH v.doctor JOIN FETCH v.patient",
            countQuery = "SELECT COUNT(v) FROM Visit v")
    Page<Visit> findAll(Pageable pageable);
}
