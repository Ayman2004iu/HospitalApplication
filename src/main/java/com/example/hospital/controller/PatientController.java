package com.example.hospital.controller;

import com.example.hospital.dto.request.PatientRequest;
import com.example.hospital.dto.response.PatientResponse;
import com.example.hospital.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@RequestBody PatientRequest request) {
        return ResponseEntity.ok(patientService.createPatient(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{nationalId}")
    public ResponseEntity<PatientResponse> getPatientByNationalId(@PathVariable Long nationalId) {
        return ResponseEntity.ok(patientService.getPatientByNationalId(nationalId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {

        return ResponseEntity.ok(patientService.getAllPatients());
    }
}
