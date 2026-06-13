package com.aymanibrahim.hospital.mapper;

import com.aymanibrahim.hospital.dto.request.DepartmentRequest;
import com.aymanibrahim.hospital.dto.response.DepartmentResponse;
import com.aymanibrahim.hospital.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentResponse toResponse(Department department);

    Department toEntity(DepartmentRequest request);
}
