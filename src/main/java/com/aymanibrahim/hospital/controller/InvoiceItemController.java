package com.aymanibrahim.hospital.controller;

import com.aymanibrahim.hospital.dto.request.InvoiceItemRequest;
import com.aymanibrahim.hospital.dto.response.InvoiceItemResponse;
import com.aymanibrahim.hospital.service.InvoiceItemService;
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
@RequestMapping("/api/invoice-items")
@RequiredArgsConstructor
public class InvoiceItemController {

    private final InvoiceItemService itemService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceItemResponse> addInvoiceItem(@Valid @RequestBody InvoiceItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.addInvoiceItem(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceItemResponse> getInvoiceItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getInvoiceItemById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InvoiceItemResponse>> getAllInvoiceItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(itemService.getAllInvoiceItems(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInvoiceItem(@PathVariable Long id) {
        itemService.deleteInvoiceItem(id);
        return ResponseEntity.noContent().build();
    }
}