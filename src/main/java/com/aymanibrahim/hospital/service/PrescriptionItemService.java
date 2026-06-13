package com.aymanibrahim.hospital.service;

import com.aymanibrahim.hospital.dto.request.PrescriptionItemRequest;
import com.aymanibrahim.hospital.dto.response.PrescriptionItemResponse;
import com.aymanibrahim.hospital.entity.Prescription;
import com.aymanibrahim.hospital.entity.PrescriptionItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PrescriptionItemService {
    PrescriptionItemResponse addPrescriptionItem(PrescriptionItemRequest request);
    PrescriptionItemResponse getPrescriptionItemById(Long id);
    Page<PrescriptionItemResponse> getAllPrescriptionItems(Pageable pageable);
    PrescriptionItemResponse updatePrescriptionItem(Long id, PrescriptionItemRequest request);
    PrescriptionItem buildItem(Prescription prescription, PrescriptionItemRequest request);
}