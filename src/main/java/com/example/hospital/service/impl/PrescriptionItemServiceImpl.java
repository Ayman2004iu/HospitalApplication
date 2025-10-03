package com.example.hospital.service.impl;


import com.example.hospital.dto.request.PrescriptionItemRequest;
import com.example.hospital.entity.*;
import com.example.hospital.enums.PrescriptionItemStatus;
import com.example.hospital.repository.*;
import com.example.hospital.dto.response.PrescriptionItemResponse;
import com.example.hospital.mapper.PrescriptionItemMapper;
import com.example.hospital.entity.Medication;
import com.example.hospital.entity.PrescriptionItem;
import com.example.hospital.service.PrescriptionItemService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionItemServiceImpl implements PrescriptionItemService {

    private final PrescriptionItemRepository itemRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicationRepository medicationRepository;
    private final PrescriptionItemMapper itemMapper;

    @Override
    public PrescriptionItemResponse addPrescriptionItem(PrescriptionItemRequest request) {
        Prescription prescription = prescriptionRepository.findById(request.getPrescriptionId())
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        Medication medication = medicationRepository.findById(request.getMedicationId())
                .orElseThrow(() -> new RuntimeException("Medication not found"));

        BigDecimal price = medication.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        PrescriptionItemStatus status;
        if (medication.getQuantityAvailable() == null || medication.getQuantityAvailable() < request.getQuantity()) {
            status = PrescriptionItemStatus.OUT_OF_STOCK;
        } else {
            status = PrescriptionItemStatus.PENDING;
        }

        PrescriptionItem item = PrescriptionItem.builder()
                .prescription(prescription)
                .medication(medication)
                .dosage(request.getDosage())
                .frequency(request.getFrequency())
                .durationDays(request.getDurationDays())
                .quantity(request.getQuantity())
                .price(price)
                .status(status)
                .build();

        return itemMapper.toResponse(itemRepository.save(item));
    }

    @Override
    public PrescriptionItemResponse getPrescriptionItemById(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("PrescriptionItem not found"));
    }

    @Override
    public List<PrescriptionItemResponse> getAllPrescriptionItems() {
        return itemRepository.findAll()
                .stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @Override
    public PrescriptionItemResponse updatePrescriptionItem(Long id, PrescriptionItemRequest request) {
        PrescriptionItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PrescriptionItem not found"));

        Medication medication = medicationRepository.findById(request.getMedicationId())
                .orElseThrow(() -> new RuntimeException("Medication not found"));

        if (request.getQuantity() != null) {
            item.setQuantity(request.getQuantity());
            item.setPrice(medication.getUnitPrice()
                    .multiply(java.math.BigDecimal.valueOf(request.getQuantity())));
        }

        if (request.getDosage() != null) item.setDosage(request.getDosage());
        if (request.getFrequency() != null) item.setFrequency(request.getFrequency());
        if (request.getDurationDays() != 0) item.setDurationDays(request.getDurationDays());

        item.setMedication(medication);

        return itemMapper.toResponse(itemRepository.save(item));
    }
}
