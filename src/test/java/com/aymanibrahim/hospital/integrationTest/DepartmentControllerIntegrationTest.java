package com.aymanibrahim.hospital.integrationTest;

import com.aymanibrahim.hospital.dto.request.DepartmentRequest;
import com.aymanibrahim.hospital.entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DepartmentControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void createDepartment_ShouldReturn201_WhenValidRequest() throws Exception {
        DepartmentRequest request = new DepartmentRequest("Oncology", "Cancer treatment");

        mockMvc.perform(post("/api/departments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Oncology"));
    }

    @Test
    void createDepartment_ShouldReturn400_WhenDuplicateName() throws Exception {
        createDepartment("Pediatrics");

        DepartmentRequest request = new DepartmentRequest("Pediatrics", "desc");

        mockMvc.perform(post("/api/departments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDepartment_ShouldReturn400_WhenNameBlank() throws Exception {
        DepartmentRequest request = new DepartmentRequest("", "desc");

        mockMvc.perform(post("/api/departments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDepartment_ShouldReturn200_WhenExists() throws Exception {
        Department dept = createDepartment("Radiology");

        mockMvc.perform(get("/api/departments/" + dept.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Radiology"));
    }

    @Test
    void getDepartment_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/departments/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllDepartments_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/departments")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void updateDepartment_ShouldReturn200_WhenValidRequest() throws Exception {
        Department dept = createDepartment("OldDeptName");
        DepartmentRequest request = new DepartmentRequest("UpdatedDeptName", "new desc");

        mockMvc.perform(put("/api/departments/" + dept.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedDeptName"));
    }

    @Test
    void updateDepartment_ShouldReturn400_WhenNameTakenByOther() throws Exception {
        Department deptA = createDepartment("DeptAlpha");
        createDepartment("DeptBeta");

        DepartmentRequest request = new DepartmentRequest("DeptBeta", "desc");

        mockMvc.perform(put("/api/departments/" + deptA.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteDepartment_ShouldReturn204_WhenExists() throws Exception {
        Department dept = createDepartment("TempDept");

        mockMvc.perform(delete("/api/departments/" + dept.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDepartment_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/departments/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void anyDeptEndpoint_ShouldReturn401_WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isUnauthorized());
    }
}