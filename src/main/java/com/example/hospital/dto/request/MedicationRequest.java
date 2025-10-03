package com.example.hospital.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationRequest {
    private String name;
    private String code;
    private Integer quantityAvailable;
    private BigDecimal unitPrice;
}
