package com.example.hospital.dto.request;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionRequest {
    private Long doctorId;
    private Long patientId;
    private Long visitId;
    private String notes;
    private List<PrescriptionItemRequest> items;
}
