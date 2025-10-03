package com.example.hospital.mapper;

import com.example.hospital.dto.response.PrescriptionResponse;
import com.example.hospital.entity.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PrescriptionItemMapper.class})
public interface PrescriptionMapper {

    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "visit.id", target = "visitId")
    PrescriptionResponse toResponse(Prescription prescription);

    List<PrescriptionResponse> toResponseList(List<Prescription> prescriptions);
}
