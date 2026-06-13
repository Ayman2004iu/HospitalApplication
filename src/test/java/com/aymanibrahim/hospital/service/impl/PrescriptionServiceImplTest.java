package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.PrescriptionItemRequest;
import com.aymanibrahim.hospital.dto.request.PrescriptionRequest;
import com.aymanibrahim.hospital.dto.response.PrescriptionResponse;
import com.aymanibrahim.hospital.entity.*;
import com.aymanibrahim.hospital.enums.PaymentStatus;
import com.aymanibrahim.hospital.enums.PrescriptionItemStatus;
import com.aymanibrahim.hospital.enums.PrescriptionStatus;
import com.aymanibrahim.hospital.enums.VisitStatus;
import com.aymanibrahim.hospital.repository.*;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.PrescriptionMapper;
import com.aymanibrahim.hospital.service.PrescriptionItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrescriptionServiceImplTest {

    @Mock private PrescriptionRepository prescriptionRepository;
    @Mock private VisitRepository visitRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private UserRepository userRepository;
    @Mock private PrescriptionMapper prescriptionMapper;
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private PrescriptionItemService prescriptionItemService;

    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("doctor@hospital.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private User buildUser() {
        User u = new User();
        u.setEmail("doctor@hospital.com");
        return u;
    }

    private Doctor buildDoctor() {
        Doctor d = new Doctor();
        return d;
    }

    private Patient buildPatient() {
        Patient p = new Patient();
        return p;
    }

    private Visit buildVisit(VisitStatus status) {
        Visit v = new Visit();
        v.setStatus(status);

        Invoice invoice = new Invoice();
        invoice.setTotal(BigDecimal.valueOf(150));
        invoice.setPaymentStatus(PaymentStatus.UNPAID);
        invoice.setLines(new ArrayList<>());
        v.setInvoice(invoice);

        return v;
    }

    private PrescriptionItemRequest buildItemRequest() {
        PrescriptionItemRequest req = new PrescriptionItemRequest();
        req.setMedicationId(1L);
        req.setDosage("1 tablet");
        req.setFrequency("twice daily");
        req.setDurationDays(7);
        req.setQuantity(14);
        return req;
    }

    private PrescriptionRequest buildPrescriptionRequest(Long visitId) {
        PrescriptionRequest req = new PrescriptionRequest();
        req.setPatientId(1L);
        req.setVisitId(visitId);
        req.setNotes("notes");
        req.setItems(List.of(buildItemRequest()));
        return req;
    }

    private void mockDoctorLookup() {
        when(userRepository.findByEmail("doctor@hospital.com")).thenReturn(Optional.of(buildUser()));
        when(doctorRepository.findByUser(any())).thenReturn(Optional.of(buildDoctor()));
    }

    @Test
    void createPrescription_ShouldCreate_WhenVisitIsOpen() {
        PrescriptionRequest request = buildPrescriptionRequest(1L);
        Visit visit = buildVisit(VisitStatus.OPEN);
        PrescriptionItem item = new PrescriptionItem();
        item.setPrice(BigDecimal.valueOf(50));
        Prescription saved = new Prescription();

        mockDoctorLookup();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(buildPatient()));
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));

        when(prescriptionItemService.buildItem(any(), any())).thenReturn(item);

        when(prescriptionRepository.save(any())).thenReturn(saved);
        when(invoiceRepository.save(any())).thenReturn(visit.getInvoice());
        when(prescriptionMapper.toResponse(saved)).thenReturn(new PrescriptionResponse());

        PrescriptionResponse result = prescriptionService.createPrescription(request);
        assertNotNull(result);
        verify(prescriptionRepository).save(any());
    }

    @Test
    void createPrescription_ShouldCreate_WhenVisitIsInProgress() {
        PrescriptionRequest request = buildPrescriptionRequest(1L);
        Visit visit = buildVisit(VisitStatus.IN_PROGRESS);
        PrescriptionItem item = new PrescriptionItem();
        item.setPrice(BigDecimal.valueOf(50));

        mockDoctorLookup();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(buildPatient()));
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(prescriptionItemService.buildItem(any(), any())).thenReturn(item);
        when(prescriptionRepository.save(any())).thenReturn(new Prescription());
        when(invoiceRepository.save(any())).thenReturn(visit.getInvoice());
        when(prescriptionMapper.toResponse(any())).thenReturn(new PrescriptionResponse());

        assertDoesNotThrow(() -> prescriptionService.createPrescription(request));
    }

    @Test
    void createPrescription_ShouldThrow_WhenVisitIsClosed() {
        PrescriptionRequest request = buildPrescriptionRequest(1L);

        mockDoctorLookup();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(buildPatient()));
        when(visitRepository.findById(1L)).thenReturn(Optional.of(buildVisit(VisitStatus.CLOSED)));

        assertThrows(BusinessLogicException.class, () -> prescriptionService.createPrescription(request));
        verify(prescriptionRepository, never()).save(any());
    }

    @Test
    void createPrescription_ShouldThrow_WhenVisitIsCancelled() {
        PrescriptionRequest request = buildPrescriptionRequest(1L);

        mockDoctorLookup();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(buildPatient()));
        when(visitRepository.findById(1L)).thenReturn(Optional.of(buildVisit(VisitStatus.CANCELLED)));

        assertThrows(BusinessLogicException.class, () -> prescriptionService.createPrescription(request));
    }

    @Test
    void createPrescription_ShouldThrow_WhenCurrentUserNotDoctor() {
        PrescriptionRequest request = buildPrescriptionRequest(1L);

        when(userRepository.findByEmail("doctor@hospital.com")).thenReturn(Optional.of(buildUser()));
        when(doctorRepository.findByUser(any())).thenReturn(Optional.empty());

        assertThrows(BusinessLogicException.class, () -> prescriptionService.createPrescription(request));
    }

    @Test
    void createPrescription_ShouldThrow_WhenPatientNotFound() {
        PrescriptionRequest request = buildPrescriptionRequest(1L);

        mockDoctorLookup();
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.createPrescription(request));
    }

    @Test
    void createPrescription_ShouldThrow_WhenVisitNotFound() {
        PrescriptionRequest request = buildPrescriptionRequest(99L);

        mockDoctorLookup();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(buildPatient()));
        when(visitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.createPrescription(request));
    }

    @Test
    void createPrescription_ShouldUpdateInvoiceTotal_WithMedsCost() {
        PrescriptionRequest request = buildPrescriptionRequest(1L);
        Visit visit = buildVisit(VisitStatus.OPEN);
        visit.getInvoice().setTotal(BigDecimal.valueOf(150));

        PrescriptionItem item = new PrescriptionItem();
        item.setPrice(BigDecimal.valueOf(75));

        mockDoctorLookup();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(buildPatient()));
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(prescriptionItemService.buildItem(any(), any())).thenReturn(item);
        when(prescriptionRepository.save(any())).thenReturn(new Prescription());
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(prescriptionMapper.toResponse(any())).thenReturn(new PrescriptionResponse());

        prescriptionService.createPrescription(request);

        assertEquals(BigDecimal.valueOf(225), visit.getInvoice().getTotal());
    }

    @Test
    void dispensePrescription_ShouldDispenseAll_WhenStockSufficient() {
        Medication med = new Medication();
        med.setQuantityAvailable(20);

        PrescriptionItem item = new PrescriptionItem();
        item.setMedication(med);
        item.setQuantity(5);
        item.setStatus(PrescriptionItemStatus.PENDING);

        Prescription prescription = new Prescription();
        prescription.setItems(List.of(item));

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(prescriptionRepository.save(any())).thenReturn(prescription);

        prescriptionService.dispensePrescription(1L);

        assertEquals(PrescriptionItemStatus.DISPENSED, item.getStatus());
        assertEquals(15, med.getQuantityAvailable());
        assertEquals(PrescriptionStatus.DISPENSED, prescription.getStatus());
    }

    @Test
    void dispensePrescription_ShouldMarkOutOfStock_WhenInsufficientQuantity() {
        Medication med = new Medication();
        med.setQuantityAvailable(2);

        PrescriptionItem item = new PrescriptionItem();
        item.setMedication(med);
        item.setQuantity(10);

        Prescription prescription = new Prescription();
        prescription.setItems(List.of(item));

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(prescriptionRepository.save(any())).thenReturn(prescription);

        prescriptionService.dispensePrescription(1L);

        assertEquals(PrescriptionItemStatus.OUT_OF_STOCK, item.getStatus());
        assertEquals(PrescriptionStatus.PARTIALLY_DISPENSED, prescription.getStatus());
        assertEquals(2, med.getQuantityAvailable());
    }

    @Test
    void dispensePrescription_ShouldThrow_WhenNotFound() {
        when(prescriptionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.dispensePrescription(99L));
    }

    @Test
    void getPrescriptionById_ShouldReturn_WhenExists() {
        Prescription prescription = new Prescription();
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(prescriptionMapper.toResponse(prescription)).thenReturn(new PrescriptionResponse());

        assertNotNull(prescriptionService.getPrescriptionById(1L));
    }

    @Test
    void getPrescriptionById_ShouldThrow_WhenNotFound() {
        when(prescriptionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.getPrescriptionById(99L));
    }

    @Test
    void updatePrescription_ShouldUpdateNotes() {
        Prescription prescription = new Prescription();
        prescription.setNotes("old notes");

        PrescriptionRequest request = new PrescriptionRequest();
        request.setNotes("new notes");

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(prescriptionRepository.save(prescription)).thenReturn(prescription);
        when(prescriptionMapper.toResponse(prescription)).thenReturn(new PrescriptionResponse());

        prescriptionService.updatePrescription(1L, request);
        assertEquals("new notes", prescription.getNotes());
    }

    @Test
    void updatePrescription_ShouldThrow_WhenNotFound() {
        when(prescriptionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionService.updatePrescription(99L, new PrescriptionRequest()));
    }

    @Test
    void getAllPrescriptions_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(prescriptionRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(new Prescription())));
        when(prescriptionMapper.toResponse(any())).thenReturn(new PrescriptionResponse());

        assertEquals(1, prescriptionService.getAllPrescriptions(pageable).getTotalElements());
    }
}