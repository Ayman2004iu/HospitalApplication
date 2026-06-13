package com.aymanibrahim.hospital.dto.response;

import com.aymanibrahim.hospital.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {
    private Long id;
    private Long patientId;
    private BigDecimal total;
    private PaymentStatus paymentStatus;
    private Long visitId;
    private LocalDateTime invoiceDate;
}
