package com.aymanibrahim.hospital.controller;

import com.aymanibrahim.hospital.dto.request.InvoiceRequest;
import com.aymanibrahim.hospital.dto.response.InvoiceResponse;
import com.aymanibrahim.hospital.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/visit/{visitId}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> payInvoice(
            @PathVariable Long visitId,
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.payInvoice(visitId, request));
    }

    @GetMapping("/visit/{visitId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> getByVisitId(@PathVariable Long visitId) {
        return ResponseEntity.ok(invoiceService.getByVisitId(visitId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InvoiceResponse>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(invoiceService.getAllInvoices(pageable));
    }
}