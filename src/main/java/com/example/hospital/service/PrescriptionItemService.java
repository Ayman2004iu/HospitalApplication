package com.example.hospital.service;

import com.example.hospital.dto.request.PrescriptionItemRequest;
import com.example.hospital.dto.response.PrescriptionItemResponse;

import java.util.List;

public interface PrescriptionItemService {
    PrescriptionItemResponse addPrescriptionItem(PrescriptionItemRequest request);
    PrescriptionItemResponse getPrescriptionItemById(Long id);
    List<PrescriptionItemResponse> getAllPrescriptionItems();
    PrescriptionItemResponse updatePrescriptionItem(Long id, PrescriptionItemRequest request);
}
