package com.example.hospital.mapper;

import com.example.hospital.dto.request.DoctorRequest;
import com.example.hospital.dto.response.DoctorResponse;
import com.example.hospital.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mappings({
            @Mapping(source = "department.id", target = "departmentId"),
            @Mapping(source = "clinic.id", target = "clinicId"),
    })
    DoctorResponse toResponse(Doctor doctor);

    Doctor toEntity(DoctorRequest request);
}
