package com.example.hospital.dto.response;

import com.example.hospital.enums.PrescriptionStatus;
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
    private PrescriptionStatus status;
    private List<PrescriptionItemResponse> items;
}
