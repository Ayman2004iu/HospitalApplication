package com.aymanibrahim.hospital.service;

import com.aymanibrahim.hospital.dto.request.DoctorRequest;
import com.aymanibrahim.hospital.dto.response.DoctorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {
    DoctorResponse createDoctor(DoctorRequest request);
    DoctorResponse getDoctorById(Long id);
    Page<DoctorResponse> getAllDoctors(Pageable pageable);
    DoctorResponse updateDoctor(Long id, DoctorRequest request);
    void deleteDoctor(Long id);
}