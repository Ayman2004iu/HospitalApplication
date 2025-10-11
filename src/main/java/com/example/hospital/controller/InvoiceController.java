package com.example.hospital.controller;

import com.example.hospital.dto.request.InvoiceRequest;
import com.example.hospital.dto.response.InvoiceResponse;
import com.example.hospital.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;


    @PostMapping("/{visitId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> payInvoice(@PathVariable Long visitId,@Valid  @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.payInvoice(visitId,request));
    }

    @GetMapping("/{visitId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> getByVisitId(@PathVariable Long visitId) {
        return ResponseEntity.ok(invoiceService.getByVisitId(visitId));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }
}

