package com.example.hospital.service.impl;

import com.example.hospital.dto.request.MedicationRequest;
import com.example.hospital.dto.response.MedicationResponse;
import com.example.hospital.entity.Medication;
import com.example.hospital.mapper.MedicationMapper;
import com.example.hospital.repository.MedicationRepository;
import com.example.hospital.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {
    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    @Override
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
    public MedicationResponse getMedicationById(Long id) {
        return medicationRepository.findById(id)
                .map(medicationMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Medication not found"));
    }

    @Override
    public List<MedicationResponse> getAllMedications() {
        return medicationRepository.findAll()
                .stream()
                .map(medicationMapper::toResponse)
                .toList();
    }
}

