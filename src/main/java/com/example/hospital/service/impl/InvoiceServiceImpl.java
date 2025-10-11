package com.example.hospital.service.impl;

import com.example.hospital.dto.request.InvoiceRequest;
import com.example.hospital.dto.response.InvoiceResponse;
import com.example.hospital.entity.Invoice;
import com.example.hospital.enums.PaymentStatus;
import com.example.hospital.exception.BusinessLogicException;
import com.example.hospital.exception.ResourceNotFoundException;
import com.example.hospital.mapper.InvoiceMapper;
import com.example.hospital.repository.InvoiceRepository;
import com.example.hospital.service.InvoiceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    public InvoiceResponse getByVisitId(Long visitId) {
        Invoice invoice = invoiceRepository.findByVisit_Id(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        return invoiceMapper.toResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse payInvoice(Long visitId, InvoiceRequest request ) {
        Invoice invoice = invoiceRepository.findByVisit_Id(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BusinessLogicException("Invoice already fully paid.");
        }

        BigDecimal amount = request.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessLogicException("Payment amount must be greater than zero.");
        }

        BigDecimal total = invoice.getTotal();
        BigDecimal alreadyPaid = invoice.getPaidAmount() == null ? BigDecimal.ZERO : invoice.getPaidAmount();
        BigDecimal newPaid = alreadyPaid.add(amount);

        if (newPaid.compareTo(total) > 0) {
            throw new BusinessLogicException("Payment exceeds invoice total. Remaining: " + total.subtract(alreadyPaid));
        }

        invoice.setPaidAmount(newPaid);

        if (newPaid.compareTo(BigDecimal.ZERO) > 0 && newPaid.compareTo(total) < 0) {
            invoice.setPaymentStatus(PaymentStatus.PARTIAL);
        } else if (newPaid.compareTo(total) == 0) {
            invoice.setPaymentStatus(PaymentStatus.PAID);
        }

        Invoice saved = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(saved);
    }
    @Override
    public InvoiceResponse getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }
}
