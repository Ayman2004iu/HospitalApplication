package com.example.hospital.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItemRequest {
    private Long medicationId;
    private String dosage;
    private String frequency;
    private Integer  durationDays;
    private Long prescriptionId;
    private Integer  quantity;
}
