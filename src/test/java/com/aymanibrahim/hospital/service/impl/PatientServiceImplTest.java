package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.PatientRequest;
import com.aymanibrahim.hospital.dto.response.PatientResponse;
import com.aymanibrahim.hospital.entity.Patient;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.PatientMapper;
import com.aymanibrahim.hospital.repository.PatientRepository;
import com.aymanibrahim.hospital.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceImplTest {

    @Mock private PatientRepository patientRepository;
    @Mock private PatientMapper patientMapper;

    @InjectMocks
    private PatientServiceImpl patientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private PatientRequest buildRequest() {
        return new PatientRequest("12345", LocalDate.of(1990, 1, 1), "Male", "Cairo", "Ahmed Ali", "01012345678");
    }

    private Patient buildPatient() {
        Patient p = new Patient();
        p.setNationalId("12345");
        p.setName("Ahmed Ali");
        return p;
    }

    @Test
    void createPatient_ShouldCreate_WhenNationalIdNotExists() {
        PatientRequest request = buildRequest();
        Patient patient = buildPatient();
        PatientResponse response = new PatientResponse();

        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.empty());
        when(patientMapper.toEntity(request)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(response);

        PatientResponse result = patientService.createPatient(request);
        assertNotNull(result);
        verify(patientRepository).save(patient);
    }

    @Test
    void createPatient_ShouldThrow_WhenNationalIdExists() {
        PatientRequest request = buildRequest();
        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.of(new Patient()));

        assertThrows(BusinessLogicException.class, () -> patientService.createPatient(request));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void updatePatient_ShouldUpdate_WhenExists() {
        Patient patient = buildPatient();
        PatientRequest request = new PatientRequest("12345", LocalDate.of(1991, 5, 10), "Female", "Alexandria", "Sara Ahmed", "01098765432");

        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(new PatientResponse());

        assertNotNull(patientService.updatePatient("12345", request));
        assertEquals("Sara Ahmed", patient.getName());
        assertEquals("Female", patient.getGender());
        assertEquals("Alexandria", patient.getAddress());
    }

    @Test
    void updatePatient_ShouldThrow_WhenNotFound() {
        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> patientService.updatePatient("12345", buildRequest()));
    }

    @Test
    void getPatientByNationalId_ShouldReturn_WhenExists() {
        Patient patient = buildPatient();
        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(new PatientResponse());

        assertNotNull(patientService.getPatientByNationalId("12345"));
    }

    @Test
    void getPatientByNationalId_ShouldThrow_WhenNotFound() {
        when(patientRepository.findByNationalId("12345")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientByNationalId("12345"));
    }

    @Test
    void getAllPatients_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(patientRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(new Patient())));
        when(patientMapper.toResponse(any())).thenReturn(new PatientResponse());

        assertEquals(1, patientService.getAllPatients(pageable).getTotalElements());
    }
}