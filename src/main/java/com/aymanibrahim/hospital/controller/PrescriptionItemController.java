package com.aymanibrahim.hospital.controller;

import com.aymanibrahim.hospital.dto.request.PrescriptionItemRequest;
import com.aymanibrahim.hospital.dto.response.PrescriptionItemResponse;
import com.aymanibrahim.hospital.service.PrescriptionItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescription-items")
@RequiredArgsConstructor
public class PrescriptionItemController {

    private final PrescriptionItemService itemService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionItemResponse> addItem(
            @Valid @RequestBody PrescriptionItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.addPrescriptionItem(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PrescriptionItemResponse> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getPrescriptionItemById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PrescriptionItemResponse>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(itemService.getAllPrescriptionItems(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionItemResponse> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody PrescriptionItemRequest request) {
        return ResponseEntity.ok(itemService.updatePrescriptionItem(id, request));
    }
}