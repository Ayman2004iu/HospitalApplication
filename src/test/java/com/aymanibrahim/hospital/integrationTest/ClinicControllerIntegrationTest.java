package com.aymanibrahim.hospital.integrationTest;

import com.aymanibrahim.hospital.dto.request.ClinicRequest;
import com.aymanibrahim.hospital.dto.request.RegisterRequest;
import com.aymanibrahim.hospital.entity.Clinic;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClinicControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void createClinic_ShouldReturn201_WhenValidRequest() throws Exception {
        ClinicRequest request = new ClinicRequest("Neurology", "Brain clinic", "Floor 3");

        mockMvc.perform(post("/api/clinics")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Neurology"))
                .andExpect(jsonPath("$.location").value("Floor 3"));
    }

    @Test
    void createClinic_ShouldReturn400_WhenDuplicateName() throws Exception {
        createClinic("Orthopedics");

        ClinicRequest request = new ClinicRequest("Orthopedics", "desc", "Floor 2");

        mockMvc.perform(post("/api/clinics")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClinic_ShouldReturn400_WhenNameBlank() throws Exception {
        ClinicRequest request = new ClinicRequest("", "desc", "Floor 1");

        mockMvc.perform(post("/api/clinics")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getClinicById_ShouldReturn200_WhenExists() throws Exception {
        Clinic clinic = createClinic("Dermatology");

        mockMvc.perform(get("/api/clinics/" + clinic.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dermatology"));
    }

    @Test
    void getClinicById_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/clinics/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllClinics_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/clinics")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void updateClinic_ShouldReturn200_WhenValidRequest() throws Exception {
        Clinic clinic = createClinic("OldClinicName");
        ClinicRequest request = new ClinicRequest("UpdatedClinicName", "new desc", "Floor 5");

        mockMvc.perform(put("/api/clinics/" + clinic.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedClinicName"));
    }

    @Test
    void updateClinic_ShouldReturn400_WhenNameTakenByOther() throws Exception {
        Clinic clinicA = createClinic("ClinicAlpha");
        createClinic("ClinicBeta");

        ClinicRequest request = new ClinicRequest("ClinicBeta", "desc", "Floor 1");

        mockMvc.perform(put("/api/clinics/" + clinicA.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteClinic_ShouldReturn204_WhenExists() throws Exception {
        Clinic clinic = createClinic("TempClinic");

        mockMvc.perform(delete("/api/clinics/" + clinic.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteClinic_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/clinics/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void anyClinicEndpoint_ShouldReturn403_WhenRoleInsufficient() throws Exception {
        RegisterRequest reg = new RegisterRequest(
                "patient1", "patient1@test.com", "pass123", "Patient One");
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andReturn();
        String patientToken = objectMapper.readTree(
                regResult.getResponse().getContentAsString()).get("token").asText();

        mockMvc.perform(get("/api/clinics")
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isForbidden());
    }
}