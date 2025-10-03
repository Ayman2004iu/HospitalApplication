package com.example.hospital.dto.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRequest {
    private Long nationalId;
    private Long doctorId;
    private Long departmentId;
    private Long clinicId;
}
