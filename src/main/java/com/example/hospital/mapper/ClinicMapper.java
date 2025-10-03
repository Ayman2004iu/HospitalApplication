package com.example.hospital.mapper;

import com.example.hospital.dto.request.ClinicRequest;
import com.example.hospital.dto.response.ClinicResponse;
import com.example.hospital.entity.Clinic;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClinicMapper {

    ClinicResponse toResponse(Clinic clinic);

    Clinic toEntity(ClinicRequest request);
}
