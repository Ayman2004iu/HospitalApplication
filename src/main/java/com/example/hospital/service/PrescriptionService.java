package com.example.hospital.service;

import com.example.hospital.dto.request.PrescriptionRequest;
import com.example.hospital.dto.response.PrescriptionResponse;

import java.util.List;


public interface PrescriptionService {
    PrescriptionResponse createPrescription(PrescriptionRequest request);
    void dispensePrescription(Long id);
    PrescriptionResponse getPrescriptionById(Long id);
    List<PrescriptionResponse> getAllPrescriptions();
    PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request);
}

