package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.ClinicRequest;
import com.aymanibrahim.hospital.dto.response.ClinicResponse;
import com.aymanibrahim.hospital.entity.Clinic;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.ClinicMapper;
import com.aymanibrahim.hospital.repository.ClinicRepository;
import com.aymanibrahim.hospital.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;

    @Override
    @Transactional
    public ClinicResponse createClinic(ClinicRequest request) {
        if (clinicRepository.findByName(request.getName()).isPresent()) {
            throw new BusinessLogicException("Clinic with name '" + request.getName() + "' already exists");
        }
        Clinic clinic = Clinic.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .build();
        return clinicMapper.toResponse(clinicRepository.save(clinic));
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicResponse getClinicById(Long id) {
        return clinicRepository.findById(id)
                .map(clinicMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClinicResponse> getAllClinics(Pageable pageable) {
        return clinicRepository.findAll(pageable)
                .map(clinicMapper::toResponse);
    }

    @Override
    @Transactional
    public ClinicResponse updateClinic(Long id, ClinicRequest request) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with ID: " + id));

        clinicRepository.findByName(request.getName())
                .filter(existing -> !Objects.equals(existing.getId(), id))
                .ifPresent(existing -> {
                    throw new BusinessLogicException("Clinic with name '" + request.getName() + "' already exists");
                });

        clinic.setName(request.getName());
        clinic.setLocation(request.getLocation());
        clinic.setDescription(request.getDescription());
        return clinicMapper.toResponse(clinicRepository.save(clinic));
    }

    @Override
    @Transactional
    public void deleteClinic(Long id) {
        if (!clinicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Clinic not found with ID: " + id);
        }
        clinicRepository.deleteById(id);
    }
}