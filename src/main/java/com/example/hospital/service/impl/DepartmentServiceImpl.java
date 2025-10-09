package com.example.hospital.service.impl;


import com.example.hospital.dto.request.DepartmentRequest;
import com.example.hospital.dto.response.DepartmentResponse;
import com.example.hospital.entity.Department;
import com.example.hospital.exception.BusinessLogicException;
import com.example.hospital.exception.ResourceNotFoundException;
import com.example.hospital.mapper.DepartmentMapper;
import com.example.hospital.repository.DepartmentRepository;
import com.example.hospital.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BusinessLogicException("Department name cannot be empty");
        }

        if (departmentRepository.findByName(request.getName()).isPresent()) {
            throw new BusinessLogicException("Department with this name already exists");
        }

        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Department saved = departmentRepository.save(department);
        return departmentMapper.toResponse(saved);
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .map(departmentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toResponse)
                .toList();
    }

    @Override
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        department.setName(request.getName());
        department.setDescription(request.getDescription());

        Department updated = departmentRepository.save(department);
        return departmentMapper.toResponse(updated);
    }

    @Override
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found");
        }
        departmentRepository.deleteById(id);
    }
}
