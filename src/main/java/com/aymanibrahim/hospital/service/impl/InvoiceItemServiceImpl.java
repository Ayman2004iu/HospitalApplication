package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.InvoiceItemRequest;
import com.aymanibrahim.hospital.dto.response.InvoiceItemResponse;
import com.aymanibrahim.hospital.entity.Invoice;
import com.aymanibrahim.hospital.entity.InvoiceItem;
import com.aymanibrahim.hospital.enums.PaymentStatus;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.InvoiceItemMapper;
import com.aymanibrahim.hospital.repository.InvoiceItemRepository;
import com.aymanibrahim.hospital.repository.InvoiceRepository;
import com.aymanibrahim.hospital.service.InvoiceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InvoiceItemServiceImpl implements InvoiceItemService {

    private final InvoiceItemRepository itemRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemMapper itemMapper;

    @Override
    @Transactional
    public InvoiceItemResponse addInvoiceItem(InvoiceItemRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with ID: " + request.getInvoiceId()));

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BusinessLogicException(
                    "Cannot add items to a fully paid invoice with ID: " + invoice.getId());
        }

        InvoiceItem item = InvoiceItem.builder()
                .invoice(invoice)
                .type(request.getChargeType())
                .description(request.getDescription())
                .amount(request.getAmount())
                .build();

        InvoiceItem saved = itemRepository.save(item);
        invoice.setTotal(invoice.getTotal().add(saved.getAmount()));
        invoiceRepository.save(invoice);

        return itemMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceItemResponse getInvoiceItemById(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "InvoiceItem not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceItemResponse> getAllInvoiceItems(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(itemMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteInvoiceItem(Long id) {
        InvoiceItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "InvoiceItem not found with ID: " + id));

        Invoice invoice = item.getInvoice();
        invoice.setTotal(invoice.getTotal().subtract(item.getAmount()).max(BigDecimal.ZERO));
        invoiceRepository.save(invoice);
        itemRepository.delete(item);
    }
}