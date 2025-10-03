package com.example.hospital.mapper;

import com.example.hospital.dto.request.DepartmentRequest;
import com.example.hospital.dto.response.DepartmentResponse;
import com.example.hospital.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentResponse toResponse(Department department);

    Department toEntity(DepartmentRequest request);
}
