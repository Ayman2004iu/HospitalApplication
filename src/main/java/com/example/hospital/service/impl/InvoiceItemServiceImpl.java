package com.example.hospital.service.impl;

import com.example.hospital.dto.request.InvoiceItemRequest;
import com.example.hospital.dto.response.InvoiceItemResponse;
import com.example.hospital.entity.Invoice;
import com.example.hospital.entity.InvoiceItem;
import com.example.hospital.enums.PaymentStatus;
import com.example.hospital.mapper.InvoiceItemMapper;
import com.example.hospital.repository.InvoiceItemRepository;
import com.example.hospital.repository.InvoiceRepository;
import com.example.hospital.service.InvoiceItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceItemServiceImpl implements InvoiceItemService {

    private final InvoiceItemRepository itemRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemMapper itemMapper;

    @Transactional
    @Override
    public InvoiceItemResponse addInvoiceItem(InvoiceItemRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Cannot add items to a fully paid invoice");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Item amount must be greater than zero");
        }

        InvoiceItem item = InvoiceItem.builder()
                .invoice(invoice)
                .description(request.getDescription())
                .amount(request.getAmount())
                .build();

        InvoiceItem savedItem = itemRepository.save(item);

        BigDecimal newTotal = invoice.getTotal().add(savedItem.getAmount());
        invoice.setTotal(newTotal);

        if (invoice.getPaymentStatus() == PaymentStatus.PAID && newTotal.compareTo(invoice.getTotal()) > 0) {
            invoice.setPaymentStatus(PaymentStatus.PARTIAL);
        }

        invoiceRepository.save(invoice);

        return itemMapper.toResponse(savedItem);
    }

    @Override
    public InvoiceItemResponse getInvoiceItemById(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("InvoiceItem not found"));
    }

    @Override
    public List<InvoiceItemResponse> getAllInvoiceItems() {
        return itemRepository.findAll()
                .stream()
                .map(itemMapper::toResponse)
                .toList();
    }
    @Override
    @Transactional
    public void deleteInvoiceItem(Long id) {
        InvoiceItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("InvoiceItem not found"));

        Invoice invoice = item.getInvoice();

        BigDecimal newTotal = invoice.getTotal().subtract(item.getAmount());
        invoice.setTotal(newTotal.max(BigDecimal.ZERO));

        invoiceRepository.save(invoice);
        itemRepository.delete(item);
    }
}
