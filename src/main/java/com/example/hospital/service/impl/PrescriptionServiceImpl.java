package com.example.hospital.service.impl;

import com.example.hospital.dto.request.PrescriptionItemRequest;
import com.example.hospital.dto.request.PrescriptionRequest;
import com.example.hospital.dto.response.PrescriptionResponse;
import com.example.hospital.entity.*;
import com.example.hospital.enums.*;
import com.example.hospital.mapper.PrescriptionMapper;
import com.example.hospital.repository.*;
import com.example.hospital.service.PrescriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
    private final PrescriptionItemServiceImpl prescriptionItemService;

    @Transactional
    @Override
    public PrescriptionResponse createPrescription(PrescriptionRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Visit visit = visitRepository.findById(request.getVisitId())
                .orElseThrow(() -> new RuntimeException("Visit not found"));

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

        for (PrescriptionItemRequest itReq : request.getItems()) {
            PrescriptionItem item = prescriptionItemService.createItemFromRequest(prescription, itReq);
            items.add(item);
            medsTotal = medsTotal.add(item.getPrice());
        }

        prescription.setItems(items);

        Invoice invoice = visit.getInvoice();
        if (invoice == null) {
            invoice = Invoice.builder()
                    .visit(visit)
                    .paymentStatus(PaymentStatus.UNPAID)
                    .total(BigDecimal.ZERO)
                    .lines(new ArrayList<>())
                    .build();
            visit.setInvoice(invoice);
        }

        InvoiceItem line = InvoiceItem.builder()
                .invoice(invoice)
                .type(ChargeType.MEDICATION)
                .description("Medications for visit " + visit.getId())
                .amount(medsTotal)
                .build();

        invoice.getLines().add(line);
        invoice.setTotal(invoice.getTotal().add(medsTotal));

        Prescription saved = prescriptionRepository.save(prescription);
        invoiceRepository.save(invoice);

        return prescriptionMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void dispensePrescription(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

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
    public PrescriptionResponse getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .map(prescriptionMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
    }

    @Override
    public List<PrescriptionResponse> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(prescriptionMapper::toResponse)
                .toList();
    }

    @Override
    public PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        prescription.setNotes(request.getNotes());
        return prescriptionMapper.toResponse(prescriptionRepository.save(prescription));
    }
}
