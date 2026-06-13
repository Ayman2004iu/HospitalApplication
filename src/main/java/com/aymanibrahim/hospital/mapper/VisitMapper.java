package com.aymanibrahim.hospital.mapper;

import com.aymanibrahim.hospital.dto.request.VisitRequest;
import com.aymanibrahim.hospital.dto.response.VisitResponse;
import com.aymanibrahim.hospital.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "clinic", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    Visit toEntity(VisitRequest request);

    @Mapping(source = "patient.nationalId", target = "nationalId")
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "clinic.id", target = "clinicId")
    @Mapping(source = "invoice.id", target = "invoiceId")
    VisitResponse toResponse(Visit visit);
}