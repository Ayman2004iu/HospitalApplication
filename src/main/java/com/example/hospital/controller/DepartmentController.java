package com.example.hospital.controller;

import com.example.hospital.dto.request.DepartmentRequest;
import com.example.hospital.dto.response.DepartmentResponse;
import com.example.hospital.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponse>createDepartment(@Valid  @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable Long id) {

        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {

        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id,@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.updateDepartment(id,request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {

        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
