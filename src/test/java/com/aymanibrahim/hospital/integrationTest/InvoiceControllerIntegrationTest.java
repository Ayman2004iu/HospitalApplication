package com.aymanibrahim.hospital.integrationTest;

import com.aymanibrahim.hospital.dto.request.InvoiceRequest;
import com.aymanibrahim.hospital.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InvoiceControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void payInvoice_ShouldReturn200_WhenFullPayment() throws Exception {
        Patient patient = createPatient(22222222L);
        Invoice invoice = createInvoiceForVisit(patient, BigDecimal.valueOf(300));

        InvoiceRequest request = InvoiceRequest.builder().amount(BigDecimal.valueOf(300)).build();

        mockMvc.perform(post("/api/invoices/visit/" + invoice.getVisit().getId() + "/pay")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }

    @Test
    void payInvoice_ShouldReturn200_WhenPartialPayment() throws Exception {
        Patient patient = createPatient(33333333L);
        Invoice invoice = createInvoiceForVisit(patient, BigDecimal.valueOf(300));

        InvoiceRequest request = InvoiceRequest.builder().amount(BigDecimal.valueOf(100)).build();

        mockMvc.perform(post("/api/invoices/visit/" + invoice.getVisit().getId() + "/pay")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PARTIAL"));
    }

    @Test
    void payInvoice_ShouldReturn400_WhenPaymentExceedsTotal() throws Exception {
        Patient patient = createPatient(44444444L);
        Invoice invoice = createInvoiceForVisit(patient, BigDecimal.valueOf(100));

        InvoiceRequest request = InvoiceRequest.builder().amount(BigDecimal.valueOf(500)).build();

        mockMvc.perform(post("/api/invoices/visit/" + invoice.getVisit().getId() + "/pay")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void payInvoice_ShouldReturn400_WhenAlreadyPaid() throws Exception {
        Patient patient = createPatient(44400001L);
        Invoice invoice = createInvoiceForVisit(patient, BigDecimal.valueOf(200));

        InvoiceRequest full = InvoiceRequest.builder().amount(BigDecimal.valueOf(200)).build();
        mockMvc.perform(post("/api/invoices/visit/" + invoice.getVisit().getId() + "/pay")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(full)))
                .andExpect(status().isOk());

        InvoiceRequest again = InvoiceRequest.builder().amount(BigDecimal.valueOf(50)).build();
        mockMvc.perform(post("/api/invoices/visit/" + invoice.getVisit().getId() + "/pay")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(again)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getInvoiceByVisitId_ShouldReturn200() throws Exception {
        Patient patient = createPatient(55555555L);
        Invoice invoice = createInvoiceForVisit(patient, BigDecimal.valueOf(150));

        mockMvc.perform(get("/api/invoices/visit/" + invoice.getVisit().getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(150));
    }

    @Test
    void getInvoiceByVisitId_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/invoices/visit/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllInvoices_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/invoices")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAllInvoices_ShouldReturn401_WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isUnauthorized());
    }
}
