package com.example.hospital.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorRequest {

    @NotBlank(message = "Doctor name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be between 10 and 15 digits")
    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Clinic ID is required")
    private Long clinicId;
}
