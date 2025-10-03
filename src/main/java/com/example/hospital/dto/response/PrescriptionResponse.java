package com.example.hospital.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionResponse {
    private Long id;
    private Long doctorId;
    private Long patientId;
    private Long visitId;
    private String notes;
    private List<PrescriptionItemResponse> items;
}
