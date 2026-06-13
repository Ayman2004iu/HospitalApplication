package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.MedicationRequest;
import com.aymanibrahim.hospital.dto.response.MedicationResponse;
import com.aymanibrahim.hospital.entity.Medication;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.MedicationMapper;
import com.aymanibrahim.hospital.repository.MedicationRepository;
import com.aymanibrahim.hospital.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    @Override
    @Transactional
    public MedicationResponse createMedication(MedicationRequest request) {
        Medication medication = Medication.builder()
                .name(request.getName())
                .code(request.getCode())
                .unitPrice(request.getUnitPrice())
                .quantityAvailable(request.getQuantityAvailable())
                .build();
        return medicationMapper.toResponse(medicationRepository.save(medication));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicationResponse getMedicationById(Long id) {
        return medicationRepository.findById(id)
                .map(medicationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medication not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicationResponse> getAllMedications(Pageable pageable) {
        return medicationRepository.findAll(pageable)
                .map(medicationMapper::toResponse);
    }

    @Override
    @Transactional
    public MedicationResponse updateMedication(Long id, MedicationRequest request) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medication not found with ID: " + id));
        medication.setName(request.getName());
        medication.setCode(request.getCode());
        medication.setUnitPrice(request.getUnitPrice());
        medication.setQuantityAvailable(request.getQuantityAvailable());
        return medicationMapper.toResponse(medicationRepository.save(medication));
    }

    @Override
    @Transactional
    public void deleteMedication(Long id) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medication not found with ID: " + id));

        medication.setDeleted(true);

        medicationRepository.save(medication);
    }
}