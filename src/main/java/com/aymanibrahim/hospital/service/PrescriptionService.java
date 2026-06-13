package com.aymanibrahim.hospital.service;

import com.aymanibrahim.hospital.dto.request.PrescriptionRequest;
import com.aymanibrahim.hospital.dto.response.PrescriptionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PrescriptionService {
    PrescriptionResponse createPrescription(PrescriptionRequest request);
    void dispensePrescription(Long id);
    PrescriptionResponse getPrescriptionById(Long id);
    Page<PrescriptionResponse> getAllPrescriptions(Pageable pageable);
    PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request);
}