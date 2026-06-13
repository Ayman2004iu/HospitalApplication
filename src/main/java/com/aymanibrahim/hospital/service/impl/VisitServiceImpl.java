package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.VisitRequest;
import com.aymanibrahim.hospital.dto.response.VisitResponse;
import com.aymanibrahim.hospital.entity.*;
import com.aymanibrahim.hospital.enums.PaymentStatus;
import com.aymanibrahim.hospital.enums.VisitStatus;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.VisitMapper;
import com.aymanibrahim.hospital.repository.*;
import com.aymanibrahim.hospital.service.VisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final ClinicRepository clinicRepository;
    private final VisitMapper visitMapper;

    @Value("${app.consultation-fee:150}")
    private BigDecimal consultationFee;

    @Override
    @Transactional
    public VisitResponse createVisit(VisitRequest request) {
        log.info("Starting process to create a new visit for patient national ID: {}", request.getNationalId());

        Patient patient = patientRepository.findByNationalId(request.getNationalId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Clinic clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));

        Visit visit = Visit.builder()
                .patient(patient)
                .doctor(doctor)
                .department(department)
                .clinic(clinic)
                .visitDate(LocalDateTime.now())
                .status(VisitStatus.OPEN)
                .build();

        Invoice invoice = Invoice.builder()
                .visit(visit)
                .patient(patient)
                .paymentStatus(PaymentStatus.UNPAID)
                .total(consultationFee)
                .build();

        visit.setInvoice(invoice);
        log.info("Visit created successfully for patient: {}", patient.getName());
        return visitMapper.toResponse(visitRepository.save(visit));
    }

    @Override
    @Transactional
    public void cancelVisit(Long id) {
        log.warn("Attempting to cancel visit with ID: {}", id);

        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));

        if (visit.getStatus() == VisitStatus.CLOSED) {
            throw new BusinessLogicException("Cannot cancel a closed visit");
        }
        if (visit.getStatus() == VisitStatus.CANCELLED) {
            throw new BusinessLogicException("Visit is already cancelled");
        }

        visit.setStatus(VisitStatus.CANCELLED);
        if (visit.getInvoice() != null) {
            visit.getInvoice().setPaymentStatus(PaymentStatus.VOIDED);
        }
        visitRepository.save(visit);
        log.info("Visit ID: {} cancelled successfully", id);
    }

    @Override
    @Transactional
    public void closeVisit(Long id) {
        log.info("Attempting to close visit with ID: {}", id);

        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));

        if (visit.getStatus() == VisitStatus.CLOSED) {
            throw new BusinessLogicException("Visit is already closed");
        }
        if (visit.getStatus() == VisitStatus.CANCELLED) {
            throw new BusinessLogicException("Cannot close a cancelled visit");
        }

        visit.setStatus(VisitStatus.CLOSED);
        visitRepository.save(visit);
        log.info("Visit ID: {} closed successfully", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getVisitByNationalId(String nationalId) {
        Patient patient = patientRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        return visitRepository.findAllByPatient(patient).stream().map(visitMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VisitResponse> getAllVisits(Pageable pageable) {
        return visitRepository.findAll(pageable).map(visitMapper::toResponse);
    }
}
