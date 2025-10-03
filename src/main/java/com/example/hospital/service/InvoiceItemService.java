package com.example.hospital.service;

import com.example.hospital.dto.request.InvoiceItemRequest;
import com.example.hospital.dto.response.InvoiceItemResponse;

import java.util.List;

public interface InvoiceItemService {
    InvoiceItemResponse addInvoiceItem(InvoiceItemRequest request);
    InvoiceItemResponse getInvoiceItemById(Long id);
    List<InvoiceItemResponse> getAllInvoiceItems();
    void deleteInvoiceItem(Long id);
}
