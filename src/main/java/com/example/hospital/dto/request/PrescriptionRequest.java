package com.example.hospital.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionRequest {

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Visit ID is required")
    private Long visitId;

    private String notes;

    @Valid
    @NotEmpty(message = "Prescription items are required")
    private List<PrescriptionItemRequest> items;
}
