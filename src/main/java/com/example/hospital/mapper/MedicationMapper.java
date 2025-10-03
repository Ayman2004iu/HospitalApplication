package com.example.hospital.mapper;

import com.example.hospital.dto.request.MedicationRequest;
import com.example.hospital.dto.response.MedicationResponse;
import com.example.hospital.entity.Medication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationMapper {
    Medication toEntity(MedicationRequest request);

    MedicationResponse toResponse(Medication medication);

}
