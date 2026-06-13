package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.PatientRequest;
import com.aymanibrahim.hospital.dto.response.PatientResponse;
import com.aymanibrahim.hospital.entity.Patient;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.PatientMapper;
import com.aymanibrahim.hospital.repository.PatientRepository;
import com.aymanibrahim.hospital.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    @Transactional
    public PatientResponse createPatient(PatientRequest request) {
        if (patientRepository.findByNationalId(request.getNationalId()).isPresent()) {
            throw new BusinessLogicException(
                    "Patient with national ID " + request.getNationalId() + " already exists");
        }
        return patientMapper.toResponse(patientRepository.save(patientMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(String nationalId, PatientRequest request) {
        Patient patient = patientRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with national ID: " + nationalId));

        patient.setName(request.getName());
        patient.setDob(request.getDob());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());
        patient.setGender(request.getGender());
        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientByNationalId(String nationalId) {
        return patientRepository.findByNationalId(nationalId)
                .map(patientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with national ID: " + nationalId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponse> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(patientMapper::toResponse);
    }
}