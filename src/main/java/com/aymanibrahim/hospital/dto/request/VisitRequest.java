package com.aymanibrahim.hospital.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRequest {

    @NotBlank(message = "National ID is required")
    @Pattern(regexp = "^[0-9]{14}$", message = "National ID must be 14 digits")
    private String nationalId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Clinic ID is required")
    private Long clinicId;
}