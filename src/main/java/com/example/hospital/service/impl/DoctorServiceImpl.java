package com.example.hospital.service.impl;

import com.example.hospital.dto.request.DoctorRequest;
import com.example.hospital.dto.response.DoctorResponse;
import com.example.hospital.entity.*;
import com.example.hospital.exception.BusinessLogicException;
import com.example.hospital.exception.ResourceNotFoundException;
import com.example.hospital.mapper.DoctorMapper;
import com.example.hospital.repository.*;
import com.example.hospital.service.DoctorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorMapper doctorMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClinicRepository clinicRepository;

    @Transactional
    @Override
    public DoctorResponse createDoctor(DoctorRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessLogicException("User with email " + request.getEmail() + " already exists.");
        }

        Role doctorRole = roleRepository.findByName("ROLE_DOCTOR")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        User user = User.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .fullName(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>(Set.of(doctorRole)))
                .build();

        userRepository.save(user);

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        Clinic clinic= clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));

        Doctor doctor = Doctor.builder()
                .user(user)
                .specialization(request.getSpecialization())
                .email(request.getEmail())
                .phone(request.getPhone())
                .name(request.getName())
                .licenseNumber(request.getLicenseNumber())
                .department(department)
                .clinic(clinic)
                .build();

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    public DoctorResponse getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctorMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            doctor.setDepartment(department);
        }

        if (request.getClinicId() != null) {
            Clinic clinic = clinicRepository.findById(request.getClinicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));
            doctor.setClinic(clinic);
        }

        User user = doctor.getUser();
        user.setEmail(request.getEmail());
        user.setUsername(request.getEmail());
        userRepository.save(user);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        doctor.setSpecialization(request.getSpecialization());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setName(request.getName());
        doctor.setPhone(request.getPhone());
        doctor.setEmail(request.getEmail());

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }
}

