package com.example.hospital.controller;

import com.example.hospital.dto.request.VisitRequest;
import com.example.hospital.dto.response.VisitResponse;
import com.example.hospital.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<VisitResponse> createVisit(@Valid @RequestBody VisitRequest request) {

        return ResponseEntity.ok(visitService.createVisit(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @GetMapping("/{nationalId}")
    public ResponseEntity<List<VisitResponse>>  getVisitByNationalId(@PathVariable Long nationalId) {
        return ResponseEntity.ok(visitService.getVisitByNationalId(nationalId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<VisitResponse>> getAllVisits() {

        return ResponseEntity.ok(visitService.getAllVisits());
    }
}

