package com.example.hospital.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemRequest {
    private String description;
    private Long invoiceId;
    private BigDecimal amount;
}
