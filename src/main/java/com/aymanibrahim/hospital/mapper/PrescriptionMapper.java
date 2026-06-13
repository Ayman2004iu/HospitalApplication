package com.aymanibrahim.hospital.mapper;

import com.aymanibrahim.hospital.dto.response.PrescriptionResponse;
import com.aymanibrahim.hospital.entity.Prescription;
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
