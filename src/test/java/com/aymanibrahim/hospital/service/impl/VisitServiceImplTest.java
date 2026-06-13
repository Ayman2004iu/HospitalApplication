package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.VisitRequest;
import com.aymanibrahim.hospital.dto.response.VisitResponse;
import com.aymanibrahim.hospital.entity.*;
import com.aymanibrahim.hospital.repository.*;
import com.aymanibrahim.hospital.enums.VisitStatus;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.VisitMapper;
import com.aymanibrahim.hospital.service.impl.VisitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisitServiceImplTest {

    @Mock private VisitRepository visitRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private ClinicRepository clinicRepository;
    @Mock private VisitMapper visitMapper;

    @InjectMocks
    private VisitServiceImpl visitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(visitService, "consultationFee", BigDecimal.valueOf(150));
    }

    private Patient buildPatient() {
        Patient p = new Patient();
        p.setNationalId("12345678901234");
        return p;
    }

    private Doctor buildDoctor() {
        Doctor d = new Doctor();
        return d;
    }

    private Department buildDepartment() {
        return new Department();
    }

    private Clinic buildClinic() {
        return new Clinic();
    }

    @Test
    void createVisit_ShouldCreateVisitWithOpenStatus_WhenValidRequest() {
        VisitRequest request = new VisitRequest("12345", 1L, 1L, 1L);
        Patient patient = buildPatient();
        Doctor doctor = buildDoctor();
        Department department = buildDepartment();
        Clinic clinic = buildClinic();

        Visit savedVisit = new Visit();
        savedVisit.setStatus(VisitStatus.OPEN);
        savedVisit.setPatient(patient);
        savedVisit.setDoctor(doctor);

        VisitResponse expectedResponse = new VisitResponse();
        expectedResponse.setStatus(VisitStatus.OPEN);

        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(visitRepository.save(any(Visit.class))).thenReturn(savedVisit);
        when(visitMapper.toResponse(savedVisit)).thenReturn(expectedResponse);

        VisitResponse response = visitService.createVisit(request);

        assertNotNull(response);
        assertEquals(VisitStatus.OPEN, response.getStatus());
        verify(visitRepository).save(any(Visit.class));
    }

    @Test
    void createVisit_ShouldThrow_WhenPatientNotFound() {
        VisitRequest request = new VisitRequest("12345", 1L, 1L, 1L);
        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> visitService.createVisit(request));
        verify(visitRepository, never()).save(any());
    }

    @Test
    void createVisit_ShouldThrow_WhenDoctorNotFound() {
        VisitRequest request = new VisitRequest("12345", 99L, 1L, 1L);
        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.of(buildPatient()));
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> visitService.createVisit(request));
    }

    @Test
    void createVisit_ShouldThrow_WhenDepartmentNotFound() {
        VisitRequest request = new VisitRequest("12345", 1L, 99L, 1L);
        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.of(buildPatient()));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(buildDoctor()));
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> visitService.createVisit(request));
    }

    @Test
    void createVisit_ShouldThrow_WhenClinicNotFound() {
        VisitRequest request = new VisitRequest("12345", 1L, 1L, 99L);
        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.of(buildPatient()));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(buildDoctor()));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(buildDepartment()));
        when(clinicRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> visitService.createVisit(request));
    }

    @Test
    void createVisit_ShouldCreateInvoiceWithConsultationFee() {
        VisitRequest request = new VisitRequest("12345", 1L, 1L, 1L);

        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.of(buildPatient()));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(buildDoctor()));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(buildDepartment()));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(buildClinic()));

        ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
        when(visitRepository.save(captor.capture())).thenReturn(new Visit());
        when(visitMapper.toResponse(any())).thenReturn(new VisitResponse());

        visitService.createVisit(request);

        Visit saved = captor.getValue();
        assertNotNull(saved.getInvoice());
        assertEquals(BigDecimal.valueOf(150), saved.getInvoice().getTotal());
    }

    @Test
    void getVisitByNationalId_ShouldReturnList_WhenPatientExists() {
        Patient patient = buildPatient();
        Visit v1 = new Visit();
        Visit v2 = new Visit();
        VisitResponse r1 = new VisitResponse();
        VisitResponse r2 = new VisitResponse();

        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.of(patient));
        when(visitRepository.findAllByPatient(patient)).thenReturn(List.of(v1, v2));
        when(visitMapper.toResponse(v1)).thenReturn(r1);
        when(visitMapper.toResponse(v2)).thenReturn(r2);

        List<VisitResponse> result = visitService.getVisitByNationalId("12345");

        assertEquals(2, result.size());
    }

    @Test
    void getVisitByNationalId_ShouldThrow_WhenPatientNotFound() {
        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> visitService.getVisitByNationalId("12345"));
    }

    @Test
    void getAllVisits_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Visit> page = new PageImpl<>(List.of(new Visit()));
        when(visitRepository.findAll(pageable)).thenReturn(page);
        when(visitMapper.toResponse(any())).thenReturn(new VisitResponse());

        Page<VisitResponse> result = visitService.getAllVisits(pageable);

        assertEquals(1, result.getTotalElements());
    }
}