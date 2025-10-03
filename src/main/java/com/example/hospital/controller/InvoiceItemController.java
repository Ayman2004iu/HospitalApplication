package com.example.hospital.controller;

import com.example.hospital.dto.request.InvoiceItemRequest;
import com.example.hospital.dto.response.InvoiceItemResponse;
import com.example.hospital.service.InvoiceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoice-items")
@RequiredArgsConstructor
public class InvoiceItemController {

    private final InvoiceItemService itemService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceItemResponse> addInvoiceItem(@RequestBody InvoiceItemRequest request) {
        return ResponseEntity.ok(itemService.addInvoiceItem(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceItemResponse> getInvoiceItem(@PathVariable Long id) {

        return ResponseEntity.ok(itemService.getInvoiceItemById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceItemResponse>> getAllInvoiceItems() {

        return ResponseEntity.ok(itemService.getAllInvoiceItems());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoiceItem(@PathVariable Long id) {
        itemService.deleteInvoiceItem(id);
        return ResponseEntity.noContent().build();
    }
}
