package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.InvoiceRequest;
import com.aymanibrahim.hospital.dto.response.InvoiceResponse;
import com.aymanibrahim.hospital.entity.Invoice;
import com.aymanibrahim.hospital.entity.Visit;
import com.aymanibrahim.hospital.enums.PaymentStatus;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.InvoiceMapper;
import com.aymanibrahim.hospital.repository.InvoiceRepository;
import com.aymanibrahim.hospital.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceImplTest {

    @Mock private InvoiceRepository invoiceRepository;
    @Mock private InvoiceMapper invoiceMapper;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Invoice buildInvoice(BigDecimal total, BigDecimal paid, PaymentStatus status) {
        Invoice invoice = new Invoice();
        invoice.setTotal(total);
        invoice.setPaidAmount(paid);
        invoice.setPaymentStatus(status);
        Visit visit = new Visit();
        invoice.setVisit(visit);
        return invoice;
    }

    @Test
    void payInvoice_ShouldSetStatusPaid_WhenFullPayment() {
        Invoice invoice = buildInvoice(BigDecimal.valueOf(300), BigDecimal.ZERO, PaymentStatus.UNPAID);
        InvoiceRequest request = new InvoiceRequest(BigDecimal.valueOf(300));

        when(invoiceRepository.findByVisit_Id(1L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any())).thenReturn(invoice);
        when(invoiceMapper.toResponse(invoice)).thenReturn(new InvoiceResponse());

        invoiceService.payInvoice(1L, request);

        assertEquals(PaymentStatus.PAID, invoice.getPaymentStatus());
        assertEquals(BigDecimal.valueOf(300), invoice.getPaidAmount());
        assertNotNull(invoice.getInvoiceDate());
    }

    @Test
    void payInvoice_ShouldSetStatusPartial_WhenPartialPayment() {
        Invoice invoice = buildInvoice(BigDecimal.valueOf(300), BigDecimal.ZERO, PaymentStatus.UNPAID);
        InvoiceRequest request = new InvoiceRequest(BigDecimal.valueOf(100));

        when(invoiceRepository.findByVisit_Id(1L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any())).thenReturn(invoice);
        when(invoiceMapper.toResponse(invoice)).thenReturn(new InvoiceResponse());

        invoiceService.payInvoice(1L, request);

        assertEquals(PaymentStatus.PARTIAL, invoice.getPaymentStatus());
        assertEquals(BigDecimal.valueOf(100), invoice.getPaidAmount());
    }

    @Test
    void payInvoice_ShouldAccumulatePayments_WhenCalledMultipleTimes() {
        Invoice invoice = buildInvoice(BigDecimal.valueOf(300), BigDecimal.valueOf(100), PaymentStatus.PARTIAL);
        InvoiceRequest request = new InvoiceRequest(BigDecimal.valueOf(200));

        when(invoiceRepository.findByVisit_Id(1L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any())).thenReturn(invoice);
        when(invoiceMapper.toResponse(invoice)).thenReturn(new InvoiceResponse());

        invoiceService.payInvoice(1L, request);

        assertEquals(PaymentStatus.PAID, invoice.getPaymentStatus());
        assertEquals(BigDecimal.valueOf(300), invoice.getPaidAmount());
    }

    @Test
    void payInvoice_ShouldThrow_WhenAlreadyPaid() {
        Invoice invoice = buildInvoice(BigDecimal.valueOf(300), BigDecimal.valueOf(300), PaymentStatus.PAID);
        InvoiceRequest request = new InvoiceRequest(BigDecimal.valueOf(100));

        when(invoiceRepository.findByVisit_Id(1L)).thenReturn(Optional.of(invoice));

        assertThrows(BusinessLogicException.class, () -> invoiceService.payInvoice(1L, request));
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void payInvoice_ShouldThrow_WhenPaymentExceedsTotal() {
        Invoice invoice = buildInvoice(BigDecimal.valueOf(300), BigDecimal.ZERO, PaymentStatus.UNPAID);
        InvoiceRequest request = new InvoiceRequest(BigDecimal.valueOf(500));

        when(invoiceRepository.findByVisit_Id(1L)).thenReturn(Optional.of(invoice));

        assertThrows(BusinessLogicException.class, () -> invoiceService.payInvoice(1L, request));
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void payInvoice_ShouldThrow_WhenInvoiceNotFound() {
        when(invoiceRepository.findByVisit_Id(99L)).thenReturn(Optional.empty());
        InvoiceRequest request = new InvoiceRequest(BigDecimal.valueOf(100));

        assertThrows(ResourceNotFoundException.class, () -> invoiceService.payInvoice(99L, request));
    }

    @Test
    void payInvoice_ShouldHandleNullPaidAmount() {
        Invoice invoice = buildInvoice(BigDecimal.valueOf(200), null, PaymentStatus.UNPAID);
        InvoiceRequest request = new InvoiceRequest(BigDecimal.valueOf(200));

        when(invoiceRepository.findByVisit_Id(1L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any())).thenReturn(invoice);
        when(invoiceMapper.toResponse(invoice)).thenReturn(new InvoiceResponse());

        invoiceService.payInvoice(1L, request);

        assertEquals(PaymentStatus.PAID, invoice.getPaymentStatus());
    }

    @Test
    void getByVisitId_ShouldReturnInvoice_WhenExists() {
        Invoice invoice = new Invoice();
        InvoiceResponse response = new InvoiceResponse();

        when(invoiceRepository.findByVisit_Id(1L)).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toResponse(invoice)).thenReturn(response);

        InvoiceResponse result = invoiceService.getByVisitId(1L);
        assertNotNull(result);
    }

    @Test
    void getByVisitId_ShouldThrow_WhenNotFound() {
        when(invoiceRepository.findByVisit_Id(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> invoiceService.getByVisitId(99L));
    }

    @Test
    void getInvoiceById_ShouldReturnInvoice_WhenExists() {
        Invoice invoice = new Invoice();
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toResponse(invoice)).thenReturn(new InvoiceResponse());

        assertNotNull(invoiceService.getInvoiceById(1L));
    }

    @Test
    void getInvoiceById_ShouldThrow_WhenNotFound() {
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> invoiceService.getInvoiceById(99L));
    }

    @Test
    void getAllInvoices_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(invoiceRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(new Invoice())));
        when(invoiceMapper.toResponse(any())).thenReturn(new InvoiceResponse());

        assertEquals(1, invoiceService.getAllInvoices(pageable).getTotalElements());
    }
}