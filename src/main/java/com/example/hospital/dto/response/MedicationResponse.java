package com.example.hospital.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationResponse {
    private Long id;
    private String name;
    private String code;
    private Integer quantityAvailable;
    private BigDecimal unitPrice;
}
