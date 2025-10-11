package com.example.hospital.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicRequest {

    @NotBlank(message = "Clinic name is required")
    private String name;

    private String description;

    @NotBlank(message = "Location is required")
    private String location;
}
