package com.example.hospital.service.impl;

import com.example.hospital.dto.request.VisitRequest;
import com.example.hospital.dto.response.VisitResponse;
import com.example.hospital.entity.*;
import com.example.hospital.enums.PaymentStatus;
import com.example.hospital.enums.VisitStatus;
import com.example.hospital.mapper.VisitMapper;
import com.example.hospital.repository.*;
import com.example.hospital.service.VisitService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final ClinicRepository clinicRepository;
    private final VisitMapper visitMapper;

    private static final BigDecimal CONSULTATION_FEE = BigDecimal.valueOf(150);

    @Transactional
    @Override
    public VisitResponse createVisit(VisitRequest request) {
        Patient patient = patientRepository.findByNationalId(request.getNationalId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));
        Clinic clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new RuntimeException("Clinic not found"));

        Visit visit = Visit.builder()
                .patient(patient)
                .doctor(doctor)
                .department(department)
                .clinic(clinic)
                .visitDate(LocalDateTime.now())
                .status(VisitStatus.IN_PROGRESS)
                .build();

        Invoice invoice = Invoice.builder()
                .visit(visit)
                .paymentStatus(PaymentStatus.UNPAID)
                .total(CONSULTATION_FEE)
                .patient(patient)
                .build();
        visit.setInvoice(invoice);

        return visitMapper.toResponse(visitRepository.save(visit));
    }

    @Override
    public List<VisitResponse>  getVisitByNationalId(Long nationalId) {
        Patient patient = patientRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return visitRepository.findAllByPatient(patient).stream()
                .map(visitMapper::toResponse)
                .toList();
    }

    @Override
    public List<VisitResponse> getAllVisits() {
        return visitRepository.findAll().stream()
                .map(visitMapper::toResponse)
                .toList();
    }
}
