package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.InvoiceItemRequest;
import com.aymanibrahim.hospital.dto.response.InvoiceItemResponse;
import com.aymanibrahim.hospital.entity.Invoice;
import com.aymanibrahim.hospital.entity.InvoiceItem;
import com.aymanibrahim.hospital.enums.ChargeType;
import com.aymanibrahim.hospital.enums.PaymentStatus;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.InvoiceItemMapper;
import com.aymanibrahim.hospital.repository.InvoiceItemRepository;
import com.aymanibrahim.hospital.repository.InvoiceRepository;
import com.aymanibrahim.hospital.service.impl.InvoiceItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceItemServiceImplTest {

    @Mock private InvoiceItemRepository itemRepository;
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private InvoiceItemMapper itemMapper;

    @InjectMocks
    private InvoiceItemServiceImpl invoiceItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Invoice buildInvoice(PaymentStatus status, BigDecimal total) {
        Invoice invoice = new Invoice();
        invoice.setPaymentStatus(status);
        invoice.setTotal(total);
        return invoice;
    }

    @Test
    void addInvoiceItem_ShouldAdd_AndUpdateTotal() {
        Invoice invoice = buildInvoice(PaymentStatus.UNPAID, BigDecimal.valueOf(150));
        InvoiceItemRequest request = new InvoiceItemRequest("Lab test", 1L, BigDecimal.valueOf(50), ChargeType.LAB);

        InvoiceItem saved = new InvoiceItem();
        saved.setAmount(BigDecimal.valueOf(50));
        saved.setInvoice(invoice);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(itemRepository.save(any())).thenReturn(saved);
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        when(itemMapper.toResponse(saved)).thenReturn(new InvoiceItemResponse());

        invoiceItemService.addInvoiceItem(request);

        assertEquals(BigDecimal.valueOf(200), invoice.getTotal());
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void addInvoiceItem_ShouldThrow_WhenInvoicePaid() {
        Invoice invoice = buildInvoice(PaymentStatus.PAID, BigDecimal.valueOf(300));
        InvoiceItemRequest request = new InvoiceItemRequest("Radiology", 1L, BigDecimal.valueOf(100), ChargeType.RADIOLOGY);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(BusinessLogicException.class, () -> invoiceItemService.addInvoiceItem(request));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void addInvoiceItem_ShouldThrow_WhenInvoiceNotFound() {
        InvoiceItemRequest request = new InvoiceItemRequest("Test", 99L, BigDecimal.valueOf(50), ChargeType.LAB);
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> invoiceItemService.addInvoiceItem(request));
    }

    @Test
    void deleteInvoiceItem_ShouldDelete_AndSubtractFromTotal() {
        Invoice invoice = buildInvoice(PaymentStatus.PARTIAL, BigDecimal.valueOf(200));

        InvoiceItem item = new InvoiceItem();
        item.setAmount(BigDecimal.valueOf(50));
        item.setInvoice(invoice);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);

        invoiceItemService.deleteInvoiceItem(1L);

        assertEquals(BigDecimal.valueOf(150), invoice.getTotal());
        verify(itemRepository).delete(item);
    }

    @Test
    void deleteInvoiceItem_ShouldNotGoBelowZero_WhenTotalLessThanAmount() {
        Invoice invoice = buildInvoice(PaymentStatus.UNPAID, BigDecimal.valueOf(30));

        InvoiceItem item = new InvoiceItem();
        item.setAmount(BigDecimal.valueOf(50));
        item.setInvoice(invoice);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);

        invoiceItemService.deleteInvoiceItem(1L);

        assertEquals(BigDecimal.ZERO, invoice.getTotal());
    }

    @Test
    void deleteInvoiceItem_ShouldThrow_WhenNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> invoiceItemService.deleteInvoiceItem(99L));
    }

    @Test
    void getInvoiceItemById_ShouldReturn_WhenExists() {
        InvoiceItem item = new InvoiceItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toResponse(item)).thenReturn(new InvoiceItemResponse());

        assertNotNull(invoiceItemService.getInvoiceItemById(1L));
    }

    @Test
    void getInvoiceItemById_ShouldThrow_WhenNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> invoiceItemService.getInvoiceItemById(99L));
    }

    @Test
    void getAllInvoiceItems_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(itemRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(new InvoiceItem())));
        when(itemMapper.toResponse(any())).thenReturn(new InvoiceItemResponse());

        assertEquals(1, invoiceItemService.getAllInvoiceItems(pageable).getTotalElements());
    }
}