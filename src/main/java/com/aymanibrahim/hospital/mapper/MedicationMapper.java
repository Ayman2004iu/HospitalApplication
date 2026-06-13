package com.aymanibrahim.hospital.mapper;

import com.aymanibrahim.hospital.dto.request.MedicationRequest;
import com.aymanibrahim.hospital.dto.response.MedicationResponse;
import com.aymanibrahim.hospital.entity.Medication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationMapper {
    Medication toEntity(MedicationRequest request);

    MedicationResponse toResponse(Medication medication);

}
