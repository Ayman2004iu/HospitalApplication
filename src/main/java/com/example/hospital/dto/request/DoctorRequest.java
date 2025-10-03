package com.example.hospital.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String specialization;
    private String licenseNumber;
    private Long departmentId;
    private Long clinicId;
}
