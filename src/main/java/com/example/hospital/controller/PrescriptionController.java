package com.example.hospital.controller;

import com.example.hospital.dto.request.PrescriptionRequest;
import com.example.hospital.dto.response.PrescriptionResponse;
import com.example.hospital.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionResponse> createPrescription(@Valid  @RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.createPrescription(request));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/dispense")
    public ResponseEntity<Void> dispensePrescription(@PathVariable Long id) {
        prescriptionService.dispensePrescription(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<PrescriptionResponse> getPrescription(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PrescriptionResponse>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionResponse> updatePrescription(@PathVariable Long id,@Valid @RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.updatePrescription(id, request));
    }
}

