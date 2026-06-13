package com.aymanibrahim.hospital.integrationTest;

import com.aymanibrahim.hospital.dto.request.DoctorRequest;
import com.aymanibrahim.hospital.entity.Clinic;
import com.aymanibrahim.hospital.entity.Department;
import com.aymanibrahim.hospital.entity.Doctor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DoctorControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void createDoctor_ShouldReturn201_WhenValidRequest() throws Exception {
        Department dept = createDepartment("Surgery-" + System.nanoTime());
        Clinic clinic = createClinic("SurgClinic-" + System.nanoTime());

        DoctorRequest request = DoctorRequest.builder()
                .name("Dr Khalid")
                .email("dr.khalid." + System.nanoTime() + "@hospital.com")
                .phone("01011112222")
                .password("pass123")
                .specialization("Surgery")
                .licenseNumber("LIC-K-" + System.nanoTime())
                .departmentId(dept.getId())
                .clinicId(clinic.getId())
                .build();

        mockMvc.perform(post("/api/doctors")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Dr Khalid"));
    }

    @Test
    void createDoctor_ShouldReturn400_WhenEmailAlreadyExists() throws Exception {
        Department dept = createDepartment("DeptX-" + System.nanoTime());
        Clinic clinic = createClinic("ClinicX-" + System.nanoTime());
        createDoctor("duplicate.doc@hospital.com", dept, clinic);

        DoctorRequest request = DoctorRequest.builder()
                .name("Dr Duplicate")
                .email("duplicate.doc@hospital.com")
                .phone("01099998888")
                .password("pass123")
                .specialization("Cardiology")
                .licenseNumber("LIC-D-" + System.nanoTime())
                .departmentId(dept.getId())
                .clinicId(clinic.getId())
                .build();

        mockMvc.perform(post("/api/doctors")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDoctorById_ShouldReturn200_WhenExists() throws Exception {
        Department dept = createDepartment("ENTDept-" + System.nanoTime());
        Clinic clinic = createClinic("ENTClinic-" + System.nanoTime());
        Doctor doctor = createDoctor("ent.doc@hospital.com", dept, clinic);

        mockMvc.perform(get("/api/doctors/" + doctor.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ent.doc@hospital.com"));
    }

    @Test
    void getAllDoctors_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/doctors")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void deleteDoctor_ShouldReturn204_WhenExists() throws Exception {
        Department dept = createDepartment("DelDept-" + System.nanoTime());
        Clinic clinic = createClinic("DelClinic-" + System.nanoTime());
        Doctor doctor = createDoctor("del.doc." + System.nanoTime() + "@hospital.com", dept, clinic);

        mockMvc.perform(delete("/api/doctors/" + doctor.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDoctor_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/doctors/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}