package com.aymanibrahim.hospital.mapper;

import com.aymanibrahim.hospital.dto.request.PatientRequest;
import com.aymanibrahim.hospital.dto.response.PatientResponse;
import com.aymanibrahim.hospital.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient toEntity(PatientRequest request);

    PatientResponse toResponse(Patient patient);
}
