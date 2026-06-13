package com.aymanibrahim.hospital.dto.response;

import lombok.*;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {
    private Long id;
    private String nationalId;
    private LocalDate dob;
    private String gender;
    private String address;
    private String name;
    private String phone;
}