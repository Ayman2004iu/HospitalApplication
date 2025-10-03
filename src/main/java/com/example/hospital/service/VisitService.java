package com.example.hospital.service;

import com.example.hospital.dto.request.VisitRequest;
import com.example.hospital.dto.response.VisitResponse;

import java.util.List;

public interface VisitService {
    VisitResponse createVisit(VisitRequest request);
    List<VisitResponse>  getVisitByNationalId(Long nationalId);
    List<VisitResponse> getAllVisits();
}
