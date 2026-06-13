package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.MedicationRequest;
import com.aymanibrahim.hospital.dto.response.MedicationResponse;
import com.aymanibrahim.hospital.entity.Medication;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.MedicationMapper;
import com.aymanibrahim.hospital.repository.MedicationRepository;
import com.aymanibrahim.hospital.service.impl.MedicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicationServiceImplTest {

    @Mock private MedicationRepository medicationRepository;
    @Mock private MedicationMapper medicationMapper;

    @InjectMocks
    private MedicationServiceImpl medicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private MedicationRequest buildRequest() {
        MedicationRequest req = new MedicationRequest();
        req.setName("Paracetamol");
        req.setCode("MED001");
        req.setUnitPrice(BigDecimal.valueOf(5.50));
        req.setQuantityAvailable(100);
        return req;
    }

    private Medication buildMedication() {
        Medication med = new Medication();
        med.setName("Paracetamol");
        med.setCode("MED001");
        med.setUnitPrice(BigDecimal.valueOf(5.50));
        med.setQuantityAvailable(100);
        return med;
    }


    @Test
    void createMedication_ShouldCreate_WhenValidRequest() {
        MedicationRequest request = buildRequest();
        Medication saved = buildMedication();
        MedicationResponse response = new MedicationResponse();

        when(medicationRepository.save(any(Medication.class))).thenReturn(saved);
        when(medicationMapper.toResponse(saved)).thenReturn(response);

        MedicationResponse result = medicationService.createMedication(request);

        assertNotNull(result);
        verify(medicationRepository).save(any(Medication.class));
    }

    @Test
    void createMedication_ShouldSetAllFieldsCorrectly() {
        MedicationRequest request = buildRequest();
        ArgumentCaptor<Medication> captor = ArgumentCaptor.forClass(Medication.class);

        when(medicationRepository.save(captor.capture())).thenReturn(buildMedication());
        when(medicationMapper.toResponse(any())).thenReturn(new MedicationResponse());

        medicationService.createMedication(request);

        Medication captured = captor.getValue();
        assertEquals("Paracetamol", captured.getName());
        assertEquals("MED001", captured.getCode());
        assertEquals(BigDecimal.valueOf(5.50), captured.getUnitPrice());
        assertEquals(100, captured.getQuantityAvailable());
    }

    @Test
    void createMedication_ShouldSave_WithZeroQuantity() {
        MedicationRequest request = buildRequest();
        request.setQuantityAvailable(0);

        when(medicationRepository.save(any())).thenReturn(buildMedication());
        when(medicationMapper.toResponse(any())).thenReturn(new MedicationResponse());

        assertDoesNotThrow(() -> medicationService.createMedication(request));
        verify(medicationRepository).save(any());
    }


    @Test
    void getMedicationById_ShouldReturn_WhenExists() {
        Medication medication = buildMedication();
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationMapper.toResponse(medication)).thenReturn(new MedicationResponse());

        assertNotNull(medicationService.getMedicationById(1L));
    }

    @Test
    void getMedicationById_ShouldThrow_WhenNotFound() {
        when(medicationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> medicationService.getMedicationById(99L));
    }


    @Test
    void getAllMedications_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(medicationRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(buildMedication())));
        when(medicationMapper.toResponse(any())).thenReturn(new MedicationResponse());

        Page<MedicationResponse> result = medicationService.getAllMedications(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllMedications_ShouldReturnEmptyPage_WhenNoMedications() {
        Pageable pageable = PageRequest.of(0, 10);
        when(medicationRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        Page<MedicationResponse> result = medicationService.getAllMedications(pageable);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }
}