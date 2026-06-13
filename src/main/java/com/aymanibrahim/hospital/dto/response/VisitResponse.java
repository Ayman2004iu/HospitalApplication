package com.aymanibrahim.hospital.dto.response;

import com.aymanibrahim.hospital.enums.VisitStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitResponse {
    private Long id;
    private String nationalId;
    private Long doctorId;
    private Long departmentId;
    private Long clinicId;
    private Long invoiceId;
    private LocalDateTime visitDate;
    private VisitStatus status;
}