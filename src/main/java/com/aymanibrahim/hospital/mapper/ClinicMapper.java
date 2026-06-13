package com.aymanibrahim.hospital.mapper;

import com.aymanibrahim.hospital.dto.request.ClinicRequest;
import com.aymanibrahim.hospital.dto.response.ClinicResponse;
import com.aymanibrahim.hospital.entity.Clinic;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClinicMapper {

    ClinicResponse toResponse(Clinic clinic);

    Clinic toEntity(ClinicRequest request);
}
