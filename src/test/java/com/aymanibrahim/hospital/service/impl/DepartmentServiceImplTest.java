package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.DepartmentRequest;
import com.aymanibrahim.hospital.dto.response.DepartmentResponse;
import com.aymanibrahim.hospital.entity.Department;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.DepartmentMapper;
import com.aymanibrahim.hospital.repository.DepartmentRepository;
import com.aymanibrahim.hospital.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartmentServiceImplTest {

    @Mock private DepartmentRepository departmentRepository;
    @Mock private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Department buildDepartment(Long id, String name) {
        Department d = new Department();
        d.setId(id);
        d.setName(name);
        d.setDescription("Some description");
        return d;
    }


    @Test
    void createDepartment_ShouldCreate_WhenNameNotExists() {
        DepartmentRequest request = new DepartmentRequest("Cardiology", "Heart department");
        Department saved = buildDepartment(1L, "Cardiology");
        DepartmentResponse response = new DepartmentResponse(1L, "Cardiology", "Heart department");

        when(departmentRepository.findByName("Cardiology")).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(saved);
        when(departmentMapper.toResponse(saved)).thenReturn(response);

        DepartmentResponse result = departmentService.createDepartment(request);

        assertNotNull(result);
        assertEquals("Cardiology", result.getName());
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void createDepartment_ShouldThrow_WhenNameAlreadyExists() {
        DepartmentRequest request = new DepartmentRequest("Cardiology", "desc");
        when(departmentRepository.findByName("Cardiology")).thenReturn(Optional.of(new Department()));

        assertThrows(BusinessLogicException.class, () -> departmentService.createDepartment(request));
        verify(departmentRepository, never()).save(any());
    }


    @Test
    void getDepartmentById_ShouldReturn_WhenExists() {
        Department dept = buildDepartment(1L, "Neurology");
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(departmentMapper.toResponse(dept)).thenReturn(new DepartmentResponse());

        assertNotNull(departmentService.getDepartmentById(1L));
    }

    @Test
    void getDepartmentById_ShouldThrow_WhenNotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentById(99L));
    }


    @Test
    void getAllDepartments_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(departmentRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(new Department())));
        when(departmentMapper.toResponse(any())).thenReturn(new DepartmentResponse());

        assertEquals(1, departmentService.getAllDepartments(pageable).getTotalElements());
    }


    @Test
    void updateDepartment_ShouldUpdate_WhenValidRequest() {
        Department existing = buildDepartment(1L, "OldName");
        DepartmentRequest request = new DepartmentRequest("NewName", "new desc");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(departmentRepository.findByName("NewName")).thenReturn(Optional.empty());
        when(departmentRepository.save(existing)).thenReturn(existing);
        when(departmentMapper.toResponse(existing))
                .thenReturn(new DepartmentResponse(1L, "NewName", "new desc"));

        DepartmentResponse result = departmentService.updateDepartment(1L, request);
        assertEquals("NewName", result.getName());
        verify(departmentRepository).save(existing);
    }

    @Test
    void updateDepartment_ShouldThrow_WhenNewNameTakenByAnotherDepartment() {
        Department existing = buildDepartment(1L, "DeptA");
        Department other = buildDepartment(2L, "DeptB");

        DepartmentRequest request = new DepartmentRequest("DeptB", "desc");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(departmentRepository.findByName("DeptB")).thenReturn(Optional.of(other));

        assertThrows(BusinessLogicException.class,
                () -> departmentService.updateDepartment(1L, request));
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updateDepartment_ShouldAllow_WhenSameNameSameDepartment() {
        Department existing = buildDepartment(1L, "Cardiology");
        DepartmentRequest request = new DepartmentRequest("Cardiology", "updated desc");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(departmentRepository.findByName("Cardiology")).thenReturn(Optional.of(existing));
        when(departmentRepository.save(existing)).thenReturn(existing);
        when(departmentMapper.toResponse(existing)).thenReturn(new DepartmentResponse());

        assertDoesNotThrow(() -> departmentService.updateDepartment(1L, request));
    }

    @Test
    void updateDepartment_ShouldThrow_WhenDepartmentNotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> departmentService.updateDepartment(99L, new DepartmentRequest("X", "d")));
    }


    @Test
    void deleteDepartment_ShouldDelete_WhenExists() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        departmentService.deleteDepartment(1L);
        verify(departmentRepository).deleteById(1L);
    }

    @Test
    void deleteDepartment_ShouldThrow_WhenNotFound() {
        when(departmentRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> departmentService.deleteDepartment(99L));
        verify(departmentRepository, never()).deleteById(any());
    }
}