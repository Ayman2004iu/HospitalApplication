package com.aymanibrahim.hospital.service;

import com.aymanibrahim.hospital.dto.request.InvoiceRequest;
import com.aymanibrahim.hospital.dto.response.InvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {
    InvoiceResponse getByVisitId(Long visitId);
    InvoiceResponse payInvoice(Long visitId, InvoiceRequest request);
    InvoiceResponse getInvoiceById(Long id);
    Page<InvoiceResponse> getAllInvoices(Pageable pageable);
}