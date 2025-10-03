package com.example.hospital.dto.response;

import lombok.*;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {
    private Long id;
    private Long nationalId;
    private LocalDate dob;
    private String gender;
    private String address;
    private String name;
    private String phone;
}
