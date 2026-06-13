package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.DoctorRequest;
import com.aymanibrahim.hospital.dto.response.DoctorResponse;
import com.aymanibrahim.hospital.entity.*;
import com.aymanibrahim.hospital.repository.*;
import com.aymanibrahim.hospital.enums.RoleName;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.DoctorMapper;
import com.aymanibrahim.hospital.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorServiceImplTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private ClinicRepository clinicRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private DoctorRequest buildRequest() {
        DoctorRequest req = new DoctorRequest();
        req.setName("Dr Ahmed");
        req.setEmail("dr.ahmed@hospital.com");
        req.setPhone("01012345678");
        req.setPassword("password123");
        req.setSpecialization("Cardiology");
        req.setLicenseNumber("LIC001");
        req.setDepartmentId(1L);
        req.setClinicId(1L);
        return req;
    }

    private void mockRepositoriesHappy() {
        Role doctorRole = new Role();
        doctorRole.setName(RoleName.ROLE_DOCTOR);

        Department dept = new Department();
        Clinic clinic = new Clinic();
        User user = new User();

        when(userRepository.existsByEmail("dr.ahmed@hospital.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_DOCTOR)).thenReturn(Optional.of(doctorRole));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(user);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
    }


    @Test
    void createDoctor_ShouldCreate_WhenValidRequest() {
        DoctorRequest request = buildRequest();
        mockRepositoriesHappy();

        Doctor savedDoctor = new Doctor();
        DoctorResponse response = new DoctorResponse();

        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);
        when(doctorMapper.toResponse(savedDoctor)).thenReturn(response);

        DoctorResponse result = doctorService.createDoctor(request);

        assertNotNull(result);
        verify(doctorRepository).save(any(Doctor.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createDoctor_ShouldThrow_WhenEmailAlreadyExists() {
        DoctorRequest request = buildRequest();
        when(userRepository.existsByEmail("dr.ahmed@hospital.com")).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> doctorService.createDoctor(request));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void createDoctor_ShouldThrow_WhenRoleNotFound() {
        DoctorRequest request = buildRequest();
        when(userRepository.existsByEmail("dr.ahmed@hospital.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_DOCTOR)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.createDoctor(request));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void createDoctor_ShouldThrow_WhenDepartmentNotFound() {
        DoctorRequest request = buildRequest();
        Role role = new Role();
        role.setName(RoleName.ROLE_DOCTOR);

        when(userRepository.existsByEmail("dr.ahmed@hospital.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_DOCTOR)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(new User());
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.createDoctor(request));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void createDoctor_ShouldThrow_WhenClinicNotFound() {
        DoctorRequest request = buildRequest();
        Role role = new Role();
        role.setName(RoleName.ROLE_DOCTOR);

        when(userRepository.existsByEmail("dr.ahmed@hospital.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_DOCTOR)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(new User());
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(new Department()));
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.createDoctor(request));
        verify(doctorRepository, never()).save(any());
    }


    @Test
    void getDoctorById_ShouldReturn_WhenExists() {
        Doctor doctor = new Doctor();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorMapper.toResponse(doctor)).thenReturn(new DoctorResponse());

        assertNotNull(doctorService.getDoctorById(1L));
    }

    @Test
    void getDoctorById_ShouldThrow_WhenNotFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> doctorService.getDoctorById(99L));
    }


    @Test
    void getAllDoctors_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(doctorRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(new Doctor())));
        when(doctorMapper.toResponse(any())).thenReturn(new DoctorResponse());

        assertEquals(1, doctorService.getAllDoctors(pageable).getTotalElements());
    }


    @Test
    void updateDoctor_ShouldUpdate_WhenValidRequest() {
        Doctor existing = new Doctor();
        existing.setName("Old Name");
        DoctorRequest request = buildRequest();
        request.setName("Updated Name");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(new Department()));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(new Clinic()));
        when(doctorRepository.save(existing)).thenReturn(existing);
        when(doctorMapper.toResponse(existing)).thenReturn(new DoctorResponse());

        assertNotNull(doctorService.updateDoctor(1L, request));
        assertEquals("Updated Name", existing.getName());
        verify(doctorRepository).save(existing);
    }

    @Test
    void updateDoctor_ShouldThrow_WhenDoctorNotFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.updateDoctor(99L, buildRequest()));
    }

    @Test
    void updateDoctor_ShouldThrow_WhenDepartmentNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(new Doctor()));
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.updateDoctor(1L, buildRequest()));
    }

    @Test
    void updateDoctor_ShouldThrow_WhenClinicNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(new Doctor()));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(new Department()));
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.updateDoctor(1L, buildRequest()));
    }

    @Test
    void updateDoctor_ShouldUpdateAllFields() {
        Doctor existing = new Doctor();
        DoctorRequest request = buildRequest();
        request.setPhone("01099999999");
        request.setSpecialization("Neurology");
        request.setLicenseNumber("LIC999");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(new Department()));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(new Clinic()));
        when(doctorRepository.save(existing)).thenReturn(existing);
        when(doctorMapper.toResponse(existing)).thenReturn(new DoctorResponse());

        doctorService.updateDoctor(1L, request);

        assertEquals("01099999999", existing.getPhone());
        assertEquals("Neurology", existing.getSpecialization());
        assertEquals("LIC999", existing.getLicenseNumber());
    }

    @Test
    void deleteDoctor_ShouldDelete_WhenExists() {
        when(doctorRepository.existsById(1L)).thenReturn(true);
        doctorService.deleteDoctor(1L);
        verify(doctorRepository).deleteById(1L);
    }

    @Test
    void deleteDoctor_ShouldThrow_WhenNotFound() {
        when(doctorRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> doctorService.deleteDoctor(99L));
        verify(doctorRepository, never()).deleteById(any());
    }
}