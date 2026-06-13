package com.aymanibrahim.hospital.service;

import com.aymanibrahim.hospital.dto.request.VisitRequest;
import com.aymanibrahim.hospital.dto.response.VisitResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VisitService {
    VisitResponse createVisit(VisitRequest request);
    void cancelVisit(Long id);
    void closeVisit(Long id);
    List<VisitResponse> getVisitByNationalId(String nationalId);
    Page<VisitResponse> getAllVisits(Pageable pageable);
}
