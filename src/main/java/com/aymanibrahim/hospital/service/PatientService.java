package com.aymanibrahim.hospital.service;

import com.aymanibrahim.hospital.dto.request.PatientRequest;
import com.aymanibrahim.hospital.dto.response.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    PatientResponse createPatient(PatientRequest request);
    PatientResponse updatePatient(String nationalId, PatientRequest request);
    PatientResponse getPatientByNationalId(String nationalId);
    Page<PatientResponse> getAllPatients(Pageable pageable);
}