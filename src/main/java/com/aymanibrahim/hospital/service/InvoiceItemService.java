package com.aymanibrahim.hospital.service;

import com.aymanibrahim.hospital.dto.request.InvoiceItemRequest;
import com.aymanibrahim.hospital.dto.response.InvoiceItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceItemService {
    InvoiceItemResponse addInvoiceItem(InvoiceItemRequest request);
    InvoiceItemResponse getInvoiceItemById(Long id);
    Page<InvoiceItemResponse> getAllInvoiceItems(Pageable pageable);
    void deleteInvoiceItem(Long id);
}