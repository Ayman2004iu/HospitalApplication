package com.example.hospital.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicRequest {
    private String name;
    private String description;
    private String location;
}