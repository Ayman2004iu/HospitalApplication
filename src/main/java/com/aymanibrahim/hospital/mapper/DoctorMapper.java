package com.aymanibrahim.hospital.mapper;

import com.aymanibrahim.hospital.dto.request.DoctorRequest;
import com.aymanibrahim.hospital.dto.response.DoctorResponse;
import com.aymanibrahim.hospital.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mappings({
            @Mapping(source = "department.id",   target = "departmentId"),
            @Mapping(source = "department.name", target = "departmentName"),
            @Mapping(source = "clinic.id",       target = "clinicId"),
            @Mapping(source = "clinic.name",     target = "clinicName"),
    })
    DoctorResponse toResponse(Doctor doctor);

    Doctor toEntity(DoctorRequest request);
}