package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.InvoiceRequest;
import com.aymanibrahim.hospital.dto.response.InvoiceResponse;
import com.aymanibrahim.hospital.entity.Invoice;
import com.aymanibrahim.hospital.enums.PaymentStatus;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.InvoiceMapper;
import com.aymanibrahim.hospital.repository.InvoiceRepository;
import com.aymanibrahim.hospital.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getByVisitId(Long visitId) {
        return invoiceRepository.findByVisit_Id(visitId)
                .map(invoiceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found for visit ID: " + visitId));
    }

    @Override
    @Transactional
    public InvoiceResponse payInvoice(Long visitId, InvoiceRequest request) {
        Invoice invoice = invoiceRepository.findByVisit_Id(visitId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found for visit ID: " + visitId));

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BusinessLogicException(
                    "Invoice for visit ID " + visitId + " is already fully paid");
        }

        BigDecimal total = invoice.getTotal();
        BigDecimal alreadyPaid = invoice.getPaidAmount() == null
                ? BigDecimal.ZERO : invoice.getPaidAmount();
        BigDecimal newPaid = alreadyPaid.add(request.getAmount());

        if (newPaid.compareTo(total) > 0) {
            throw new BusinessLogicException(
                    "Payment exceeds invoice total. Remaining: "
                            + total.subtract(alreadyPaid));
        }

        invoice.setPaidAmount(newPaid);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setPaymentStatus(
                newPaid.compareTo(total) == 0 ? PaymentStatus.PAID : PaymentStatus.PARTIAL);

        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable)
                .map(invoiceMapper::toResponse);
    }
}