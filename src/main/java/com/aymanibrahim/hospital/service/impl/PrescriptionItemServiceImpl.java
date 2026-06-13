package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.PrescriptionItemRequest;
import com.aymanibrahim.hospital.dto.response.PrescriptionItemResponse;
import com.aymanibrahim.hospital.entity.Medication;
import com.aymanibrahim.hospital.entity.Prescription;
import com.aymanibrahim.hospital.entity.PrescriptionItem;
import com.aymanibrahim.hospital.enums.PrescriptionItemStatus;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.PrescriptionItemMapper;
import com.aymanibrahim.hospital.repository.MedicationRepository;
import com.aymanibrahim.hospital.repository.PrescriptionItemRepository;
import com.aymanibrahim.hospital.repository.PrescriptionRepository;
import com.aymanibrahim.hospital.service.PrescriptionItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PrescriptionItemServiceImpl implements PrescriptionItemService {

    private final PrescriptionItemRepository itemRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicationRepository medicationRepository;
    private final PrescriptionItemMapper itemMapper;

    @Override
    @Transactional
    public PrescriptionItemResponse addPrescriptionItem(PrescriptionItemRequest request) {
        Prescription prescription = prescriptionRepository.findById(request.getPrescriptionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prescription not found with ID: " + request.getPrescriptionId()));
        return itemMapper.toResponse(itemRepository.save(buildItem(prescription, request)));
    }

    @Override
    public PrescriptionItem buildItem(Prescription prescription, PrescriptionItemRequest request) {
        Medication medication = medicationRepository.findById(request.getMedicationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medication not found with ID: " + request.getMedicationId()));

        BigDecimal price = medication.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        PrescriptionItemStatus status =
                (medication.getQuantityAvailable() == null
                        || medication.getQuantityAvailable() < request.getQuantity())
                        ? PrescriptionItemStatus.OUT_OF_STOCK
                        : PrescriptionItemStatus.PENDING;

        return PrescriptionItem.builder()
                .prescription(prescription)
                .medication(medication)
                .dosage(request.getDosage())
                .frequency(request.getFrequency())
                .durationDays(request.getDurationDays())
                .quantity(request.getQuantity())
                .price(price)
                .status(status)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionItemResponse getPrescriptionItemById(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PrescriptionItem not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrescriptionItemResponse> getAllPrescriptionItems(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(itemMapper::toResponse);
    }

    @Override
    @Transactional
    public PrescriptionItemResponse updatePrescriptionItem(Long id, PrescriptionItemRequest request) {
        PrescriptionItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PrescriptionItem not found with ID: " + id));
        Medication medication = medicationRepository.findById(request.getMedicationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medication not found with ID: " + request.getMedicationId()));

        if (request.getQuantity() != null) {
            item.setQuantity(request.getQuantity());
            item.setPrice(medication.getUnitPrice()
                    .multiply(BigDecimal.valueOf(request.getQuantity())));
        }
        if (request.getDosage() != null) item.setDosage(request.getDosage());
        if (request.getFrequency() != null) item.setFrequency(request.getFrequency());
        if (request.getDurationDays() != null) item.setDurationDays(request.getDurationDays());
        item.setMedication(medication);

        return itemMapper.toResponse(itemRepository.save(item));
    }
}