package com.example.hospital.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItemResponse {
    private Long id;
    private Long medicationId;
    private String dosage;
    private Long prescriptionId;
    private String frequency;
    private Integer durationDays;
    private Integer quantity;
}
