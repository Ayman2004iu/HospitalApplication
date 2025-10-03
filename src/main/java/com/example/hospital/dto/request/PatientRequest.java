package com.example.hospital.dto.request;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRequest {
    private Long nationalId;
    private LocalDate dob;
    private String gender;
    private String address;
    private String name;
    private String phone;
}
