package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.ClinicRequest;
import com.aymanibrahim.hospital.dto.response.ClinicResponse;
import com.aymanibrahim.hospital.entity.Clinic;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.ClinicMapper;
import com.aymanibrahim.hospital.repository.ClinicRepository;
import com.aymanibrahim.hospital.service.impl.ClinicServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClinicServiceImplTest {

    @Mock private ClinicRepository clinicRepository;
    @Mock private ClinicMapper clinicMapper;

    @InjectMocks
    private ClinicServiceImpl clinicService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Clinic buildClinic(Long id, String name) {
        Clinic c = new Clinic();
        c.setId(id);
        c.setName(name);
        c.setLocation("Cairo");
        return c;
    }

    @Test
    void createClinic_ShouldCreate_WhenNameNotExists() {
        ClinicRequest request = new ClinicRequest("Cardiology", "Heart clinic", "Cairo");
        Clinic clinic = buildClinic(1L, "Cardiology");
        ClinicResponse response = new ClinicResponse(1L, "Cardiology", "Heart clinic", "Cairo");

        when(clinicRepository.findByName("Cardiology")).thenReturn(Optional.empty());
        when(clinicRepository.save(any(Clinic.class))).thenReturn(clinic);
        when(clinicMapper.toResponse(clinic)).thenReturn(response);

        ClinicResponse result = clinicService.createClinic(request);

        assertNotNull(result);
        assertEquals("Cardiology", result.getName());
        verify(clinicRepository).save(any(Clinic.class));
    }

    @Test
    void createClinic_ShouldThrow_WhenNameExists() {
        ClinicRequest request = new ClinicRequest("Cardiology", "desc", "Cairo");
        when(clinicRepository.findByName("Cardiology")).thenReturn(Optional.of(new Clinic()));

        assertThrows(BusinessLogicException.class, () -> clinicService.createClinic(request));
        verify(clinicRepository, never()).save(any());
    }

    @Test
    void getClinicById_ShouldReturn_WhenExists() {
        Clinic clinic = buildClinic(1L, "Cardiology");
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(clinicMapper.toResponse(clinic)).thenReturn(new ClinicResponse());

        assertNotNull(clinicService.getClinicById(1L));
    }

    @Test
    void getClinicById_ShouldThrow_WhenNotFound() {
        when(clinicRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> clinicService.getClinicById(99L));
    }

    @Test
    void getAllClinics_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(clinicRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(new Clinic())));
        when(clinicMapper.toResponse(any())).thenReturn(new ClinicResponse());

        assertEquals(1, clinicService.getAllClinics(pageable).getTotalElements());
    }

    @Test
    void updateClinic_ShouldUpdate_WhenValidRequest() {
        Clinic existing = buildClinic(1L, "OldName");
        ClinicRequest request = new ClinicRequest("NewName", "desc", "Cairo");

        when(clinicRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clinicRepository.findByName("NewName")).thenReturn(Optional.empty());
        when(clinicRepository.save(existing)).thenReturn(existing);
        when(clinicMapper.toResponse(existing)).thenReturn(new ClinicResponse(1L, "NewName", "desc", "Cairo"));

        ClinicResponse result = clinicService.updateClinic(1L, request);
        assertEquals("NewName", result.getName());
    }

    @Test
    void updateClinic_ShouldThrow_WhenNewNameTakenByOtherClinic() {
        Clinic existing = buildClinic(1L, "ClinicA");
        Clinic other = buildClinic(2L, "ClinicB");

        ClinicRequest request = new ClinicRequest("ClinicB", "desc", "Cairo");

        when(clinicRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clinicRepository.findByName("ClinicB")).thenReturn(Optional.of(other));

        assertThrows(BusinessLogicException.class, () -> clinicService.updateClinic(1L, request));
        verify(clinicRepository, never()).save(any());
    }

    @Test
    void updateClinic_ShouldAllow_WhenSameNameSameClinic() {
        Clinic existing = buildClinic(1L, "Cardiology");

        ClinicRequest request = new ClinicRequest("Cardiology", "new desc", "Cairo");

        when(clinicRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clinicRepository.findByName("Cardiology")).thenReturn(Optional.of(existing));
        when(clinicRepository.save(existing)).thenReturn(existing);
        when(clinicMapper.toResponse(existing)).thenReturn(new ClinicResponse());

        assertDoesNotThrow(() -> clinicService.updateClinic(1L, request));
    }

    @Test
    void updateClinic_ShouldThrow_WhenClinicNotFound() {
        when(clinicRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> clinicService.updateClinic(99L, new ClinicRequest("X", "d", "L")));
    }

    @Test
    void deleteClinic_ShouldDelete_WhenExists() {
        when(clinicRepository.existsById(1L)).thenReturn(true);
        clinicService.deleteClinic(1L);
        verify(clinicRepository).deleteById(1L);
    }

    @Test
    void deleteClinic_ShouldThrow_WhenNotFound() {
        when(clinicRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> clinicService.deleteClinic(99L));
        verify(clinicRepository, never()).deleteById(any());
    }
}