package com.aymanibrahim.hospital.dto.response;

import com.aymanibrahim.hospital.enums.PrescriptionItemStatus;
import lombok.*;
import java.math.BigDecimal;

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
    private BigDecimal price;
    private PrescriptionItemStatus status;
}
