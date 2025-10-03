package com.example.hospital.service;

import com.example.hospital.dto.request.InvoiceRequest;
import com.example.hospital.dto.response.InvoiceResponse;
import java.util.List;

public interface InvoiceService {
    InvoiceResponse getByVisitId(Long visitId);
    InvoiceResponse payInvoice(Long visitId, InvoiceRequest request);
    InvoiceResponse getInvoiceById(Long id);
    List<InvoiceResponse> getAllInvoices();
}
