package com.example.hospital.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
}

