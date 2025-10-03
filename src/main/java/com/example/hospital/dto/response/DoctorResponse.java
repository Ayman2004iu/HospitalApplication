package com.example.hospital.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Long clinicId;
    private String specialization;
    private Long departmentId;
    private String licenseNumber;
    private String departmentName;
    private String clinicName;
}
