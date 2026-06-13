package com.aymanibrahim.hospital.service;

import com.aymanibrahim.hospital.dto.request.ClinicRequest;
import com.aymanibrahim.hospital.dto.response.ClinicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClinicService {
    ClinicResponse createClinic(ClinicRequest request);
    ClinicResponse getClinicById(Long id);
    Page<ClinicResponse> getAllClinics(Pageable pageable);
    ClinicResponse updateClinic(Long id, ClinicRequest request);
    void deleteClinic(Long id);
}