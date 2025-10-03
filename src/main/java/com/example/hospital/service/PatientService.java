package com.example.hospital.service;

import com.example.hospital.dto.request.PatientRequest;
import com.example.hospital.dto.response.PatientResponse;

import java.util.List;

public interface PatientService {
    PatientResponse createPatient(PatientRequest request);
    PatientResponse getPatientByNationalId(Long nationalId);
    List<PatientResponse> getAllPatients();
}
