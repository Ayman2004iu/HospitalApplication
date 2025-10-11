package com.example.hospital.controller;

import com.example.hospital.dto.request.PrescriptionItemRequest;
import com.example.hospital.dto.response.PrescriptionItemResponse;
import com.example.hospital.service.PrescriptionItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescription-items")
@RequiredArgsConstructor
public class PrescriptionItemController {

    private final PrescriptionItemService itemService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionItemResponse> addItem(@Valid @RequestBody PrescriptionItemRequest request) {
        return ResponseEntity.ok(itemService.addPrescriptionItem(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrescriptionItemResponse> getItem(@PathVariable Long id) {

        return ResponseEntity.ok(itemService.getPrescriptionItemById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PrescriptionItemResponse>> getAllItems() {

        return ResponseEntity.ok(itemService.getAllPrescriptionItems());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrescriptionItemResponse> updatePrescriptionItem(@PathVariable Long id,@Valid @RequestBody PrescriptionItemRequest request){
        return ResponseEntity.ok(itemService.updatePrescriptionItem(id,request));
    }
}
