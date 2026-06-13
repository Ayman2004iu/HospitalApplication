package com.aymanibrahim.hospital.dto.response;

import com.aymanibrahim.hospital.enums.ChargeType;
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
