package com.aymanibrahim.hospital.integrationTest;

import com.aymanibrahim.hospital.dto.request.VisitRequest;
import com.aymanibrahim.hospital.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VisitControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void createVisit_ShouldReturn201_WhenValidRequest() throws Exception {
        Patient patient = createPatient(11111111000011L);
        Department dept = createDepartment("Cardiology Dept");
        Clinic clinic   = createClinic("Cardiology Clinic");
        Doctor doctor   = createDoctor("dr.visit@hospital.com", dept, clinic);

        VisitRequest request = new VisitRequest(
                patient.getNationalId(), doctor.getId(), dept.getId(), clinic.getId());

        mockMvc.perform(post("/api/visits")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.nationalId").value("11111111000011"))
                .andExpect(jsonPath("$.invoiceId").isNotEmpty());
    }

    @Test
    void createVisit_ShouldReturn404_WhenPatientNotFound() throws Exception {
        Department dept = createDepartment("NeuroDept");
        Clinic clinic   = createClinic("NeuroClinic");
        Doctor doctor   = createDoctor("dr.neuro@hospital.com", dept, clinic);

        VisitRequest request = new VisitRequest("00000000000000", doctor.getId(), dept.getId(), clinic.getId());

        mockMvc.perform(post("/api/visits")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createVisit_ShouldReturn404_WhenDoctorNotFound() throws Exception {
        Patient patient = createPatient(22211111000022L);
        Department dept = createDepartment("OrthoDeptt");
        Clinic clinic   = createClinic("OrthoClinic");

        VisitRequest request = new VisitRequest(
                patient.getNationalId(), 99999L, dept.getId(), clinic.getId());

        mockMvc.perform(post("/api/visits")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelVisit_ShouldReturn204_WhenValidId() throws Exception {
        Patient patient = createPatient(55566600000055L);
        Department dept = createDepartment("CancelDept");
        Clinic clinic   = createClinic("CancelClinic");
        Doctor doctor   = createDoctor("dr.cancel@hospital.com", dept, clinic);
        Visit visit = createOpenVisit(patient, doctor, dept, clinic);

        mockMvc.perform(patch("/api/visits/" + visit.getId() + "/cancel")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllVisits_ShouldReturn200_WithPagedContent() throws Exception {
        mockMvc.perform(get("/api/visits")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getVisitsByPatient_ShouldReturn200_WhenPatientExists() throws Exception {
        Patient patient = createPatient(33322200000033L);
        Department dept = createDepartment("GenDept-V");
        Clinic clinic   = createClinic("GenClinic-V");
        Doctor doctor   = createDoctor("dr.gen.v@hospital.com", dept, clinic);
        createOpenVisit(patient, doctor, dept, clinic);

        mockMvc.perform(get("/api/visits/" + patient.getNationalId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getVisitsByPatient_ShouldReturn404_WhenPatientNotFound() throws Exception {
        mockMvc.perform(get("/api/visits/98765432123456")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllVisits_ShouldReturn401_WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/visits"))
                .andExpect(status().isUnauthorized());
    }
}