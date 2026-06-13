package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.PrescriptionItemRequest;
import com.aymanibrahim.hospital.dto.request.PrescriptionRequest;
import com.aymanibrahim.hospital.dto.response.PrescriptionResponse;
import com.aymanibrahim.hospital.entity.*;
import com.aymanibrahim.hospital.enums.*;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.PrescriptionMapper;
import com.aymanibrahim.hospital.repository.*;
import com.aymanibrahim.hospital.service.PrescriptionItemService;
import com.aymanibrahim.hospital.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final PrescriptionItemService prescriptionItemService;

    @Override
    @Transactional
    public PrescriptionResponse createPrescription(PrescriptionRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Doctor doctor = doctorRepository.findByUser(currentUser)
                .orElseThrow(() -> new BusinessLogicException("Current user is not a registered doctor"));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        Visit visit = visitRepository.findById(request.getVisitId())
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found with ID: " + request.getVisitId()));

        if (visit.getStatus() == VisitStatus.CLOSED || visit.getStatus() == VisitStatus.CANCELLED) {
            throw new BusinessLogicException("Cannot create prescription for a visit with status: " + visit.getStatus());
        }

        Invoice invoice = visit.getInvoice();
        if (invoice == null) {
            throw new BusinessLogicException("Visit ID " + visit.getId() + " has no associated invoice");
        }

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BusinessLogicException("Cannot add prescription to a fully paid visit");
        }

        Prescription prescription = Prescription.builder()
                .doctor(doctor)
                .visit(visit)
                .patient(patient)
                .notes(request.getNotes())
                .status(PrescriptionStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        List<PrescriptionItem> items = new ArrayList<>();
        BigDecimal medsTotal = BigDecimal.ZERO;

        for (PrescriptionItemRequest itemReq : request.getItems()) {
            PrescriptionItem item = prescriptionItemService.buildItem(prescription, itemReq);
            items.add(item);
            medsTotal = medsTotal.add(item.getPrice());
        }
        prescription.setItems(items);

        InvoiceItem line = InvoiceItem.builder()
                .invoice(invoice)
                .type(ChargeType.MEDICATION)
                .description("Medications for visit #" + visit.getId())
                .amount(medsTotal)
                .build();

        invoice.getLines().add(line);
        invoice.setTotal(invoice.getTotal().add(medsTotal));

        Prescription saved = prescriptionRepository.save(prescription);
        invoiceRepository.save(invoice);

        return prescriptionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void dispensePrescription(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));

        boolean allDispensed = true;
        for (PrescriptionItem item : prescription.getItems()) {
            Medication med = item.getMedication();

            if (med.getQuantityAvailable() == null || med.getQuantityAvailable() < item.getQuantity()) {
                item.setStatus(PrescriptionItemStatus.OUT_OF_STOCK);
                allDispensed = false;
                continue;
            }
            med.setQuantityAvailable(med.getQuantityAvailable() - item.getQuantity());
            item.setStatus(PrescriptionItemStatus.DISPENSED);
        }

        prescription.setStatus(allDispensed ? PrescriptionStatus.DISPENSED : PrescriptionStatus.PARTIALLY_DISPENSED);
        prescriptionRepository.save(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .map(prescriptionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrescriptionResponse> getAllPrescriptions(Pageable pageable) {
        return prescriptionRepository.findAll(pageable)
                .map(prescriptionMapper::toResponse);
    }

    @Override
    @Transactional
    public PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));

        prescription.setNotes(request.getNotes());
        return prescriptionMapper.toResponse(prescriptionRepository.save(prescription));
    }
}