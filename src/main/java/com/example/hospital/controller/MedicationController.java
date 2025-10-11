package com.example.hospital.controller;

import com.example.hospital.dto.request.MedicationRequest;
import com.example.hospital.dto.response.MedicationResponse;
import com.example.hospital.service.MedicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicationResponse> createMedication(@Valid  @RequestBody MedicationRequest request) {
        return ResponseEntity.ok(medicationService.createMedication(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicationResponse> getMedication(@PathVariable Long id) {

        return ResponseEntity.ok(medicationService.getMedicationById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MedicationResponse>> getAllMedications() {

        return ResponseEntity.ok(medicationService.getAllMedications());
    }
}

