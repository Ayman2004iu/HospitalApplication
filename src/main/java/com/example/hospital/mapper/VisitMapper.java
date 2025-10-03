package com.example.hospital.mapper;

import com.example.hospital.dto.response.VisitResponse;
import com.example.hospital.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitMapper {

    @Mapping(source = "patient.nationalId", target = "nationalId")
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "clinic.id", target = "clinicId")
    @Mapping(source = "invoice.id", target = "invoiceId")
    VisitResponse toResponse(Visit visit);
}