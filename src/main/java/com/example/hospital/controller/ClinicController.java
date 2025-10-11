package com.example.hospital.controller;

import com.example.hospital.dto.request.ClinicRequest;
import com.example.hospital.dto.response.ClinicResponse;
import com.example.hospital.service.ClinicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService clinicService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClinicResponse> createClinic(@Valid  @RequestBody ClinicRequest request) {
        return ResponseEntity.ok(clinicService.createClinic(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClinicResponse> getClinic(@PathVariable Long id) {

        return ResponseEntity.ok(clinicService.getClinicById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClinicResponse>> getAllClinics() {

        return ResponseEntity.ok(clinicService.getAllClinics());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ClinicResponse> updateClinic(@PathVariable Long id,@Valid @RequestBody ClinicRequest request){
        return ResponseEntity.ok(clinicService.updateClinic(id,request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinic(@PathVariable Long id){

        clinicService.deleteClinic(id);
        return ResponseEntity.noContent().build();
    }
}
