package com.example.hospital.dto.response;

import com.example.hospital.enums.ChargeType;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemResponse {
    private Long id;
    private Long invoiceId;
    private ChargeType type;
    private String description;
    private BigDecimal amount;
}
