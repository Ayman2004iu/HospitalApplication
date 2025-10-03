package com.example.hospital.dto.response;

import com.example.hospital.enums.VisitStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitResponse {
    private Long id;
    private Long nationalId;
    private Long doctorId;
    private Long departmentId;
    private Long clinicId;
    private Long invoiceId;
    private LocalDateTime visitDate;
    private VisitStatus status;
}