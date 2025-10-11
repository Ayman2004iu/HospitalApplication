package com.example.hospital.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRequest {

    @NotNull(message = "National ID is required")
    private Long nationalId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Clinic ID is required")
    private Long clinicId;
}
