package com.aymanibrahim.hospital.mapper;

import com.aymanibrahim.hospital.dto.request.PrescriptionItemRequest;
import com.aymanibrahim.hospital.dto.response.PrescriptionItemResponse;
import com.aymanibrahim.hospital.entity.PrescriptionItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrescriptionItemMapper {
    PrescriptionItem toEntity(PrescriptionItemRequest request);

    @Mapping(source = "prescription.id", target = "prescriptionId")
    @Mapping(source = "medication.id", target = "medicationId")
    PrescriptionItemResponse toResponse(PrescriptionItem prescriptionItem);
}
