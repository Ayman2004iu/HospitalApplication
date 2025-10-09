package com.example.hospital.service.impl;

import com.example.hospital.dto.request.PatientRequest;
import com.example.hospital.dto.response.PatientResponse;
import com.example.hospital.entity.Patient;
import com.example.hospital.exception.ResourceNotFoundException;
import com.example.hospital.mapper.PatientMapper;
import com.example.hospital.repository.PatientRepository;
import com.example.hospital.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    @Override
    public PatientResponse createPatient(PatientRequest request) {
        Patient patient = patientRepository.findByNationalId(request.getNationalId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

            patientRepository.findByNationalId(request.getNationalId())
                .map(p -> {
                    p.setName(request.getName());
                    p.setDob(request.getDob());
                    p.setPhone(request.getPhone());
                    p.setAddress(request.getAddress());
                    p.setGender(request.getGender());
                    return p;
                }).orElseGet(() -> patientMapper.toEntity(request));

        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    public PatientResponse getPatientByNationalId(Long nationalId) {
        return patientRepository.findByNationalId(nationalId)
                .map(patientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toResponse)
                .toList();
    }
}
