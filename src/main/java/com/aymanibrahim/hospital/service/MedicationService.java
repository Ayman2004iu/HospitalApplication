package com.aymanibrahim.hospital.service;

import com.aymanibrahim.hospital.dto.request.MedicationRequest;
import com.aymanibrahim.hospital.dto.response.MedicationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicationService {
    MedicationResponse createMedication(MedicationRequest request);
    MedicationResponse getMedicationById(Long id);
    Page<MedicationResponse> getAllMedications(Pageable pageable);
    MedicationResponse updateMedication(Long id, MedicationRequest request);
    void deleteMedication(Long id);
}
