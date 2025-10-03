package com.example.hospital.service.impl;


import com.example.hospital.dto.request.ClinicRequest;
import com.example.hospital.dto.response.ClinicResponse;
import com.example.hospital.entity.Clinic;
import com.example.hospital.mapper.ClinicMapper;
import com.example.hospital.repository.ClinicRepository;
import com.example.hospital.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;

    @Override
    public ClinicResponse createClinic(ClinicRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Clinic name cannot be empty");
        }

        if (clinicRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Clinic with this name already exists");
        }

        Clinic clinic = Clinic.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .build();

        Clinic saved = clinicRepository.save(clinic);
        return clinicMapper.toResponse(saved);
    }

    @Override
    public ClinicResponse getClinicById(Long id) {
        return clinicRepository.findById(id)
                .map(clinicMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Clinic not found"));
    }

    @Override
    public List<ClinicResponse> getAllClinics() {
        return clinicRepository.findAll()
                .stream()
                .map(clinicMapper::toResponse)
                .toList();
    }


    @Override
    public ClinicResponse updateClinic(Long id, ClinicRequest request) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clinic not found"));

        clinic.setName(request.getName());
        clinic.setLocation(request.getLocation());
        clinic.setDescription(request.getDescription());

        Clinic updated = clinicRepository.save(clinic);
        return clinicMapper.toResponse(updated);
    }

    @Override
    public void deleteClinic(Long id) {
        if (!clinicRepository.existsById(id)) {
            throw new RuntimeException("Clinic not found");
        }
        clinicRepository.deleteById(id);
    }
}
