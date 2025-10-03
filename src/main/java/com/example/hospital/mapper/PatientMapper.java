package com.example.hospital.mapper;

import com.example.hospital.dto.request.PatientRequest;
import com.example.hospital.dto.response.PatientResponse;
import com.example.hospital.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient toEntity(PatientRequest request);

    PatientResponse toResponse(Patient patient);
}
