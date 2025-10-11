package com.example.hospital.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItemRequest {

    @NotNull(message = "Medication ID is required")
    private Long medicationId;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotBlank(message = "Frequency is required")
    private String frequency;

    @Positive(message = "Duration must be positive")
    private Integer durationDays;

    private Long prescriptionId;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
