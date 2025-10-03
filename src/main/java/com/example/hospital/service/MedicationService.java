package com.example.hospital.service;

import com.example.hospital.dto.request.MedicationRequest;
import com.example.hospital.dto.response.MedicationResponse;

import java.util.List;


public interface MedicationService {
    MedicationResponse createMedication(MedicationRequest request);
    MedicationResponse getMedicationById(Long id);
    List<MedicationResponse> getAllMedications();
}

