package com.aymanibrahim.hospital.integrationTest;

import com.aymanibrahim.hospital.dto.request.PatientRequest;
import com.aymanibrahim.hospital.dto.request.RegisterRequest;
import com.aymanibrahim.hospital.entity.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PatientControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void createPatient_ShouldReturn201_WhenValidRequest() throws Exception {
        PatientRequest request = new PatientRequest(
                "12345678901234", LocalDate.of(1985, 3, 15),
                "Female", "Alexandria", "Fatma Hassan", "01087654321");

        mockMvc.perform(post("/api/patients")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nationalId").value("12345678901234"))
                .andExpect(jsonPath("$.name").value("Fatma Hassan"));
    }

    @Test
    void createPatient_ShouldReturn400_WhenDuplicateNationalId() throws Exception {
        createPatient(88888888000000L);

        PatientRequest request = new PatientRequest(
                "88888888000000", LocalDate.of(1990, 1, 1),
                "Male", "Cairo", "Ahmed", "01012345678");

        mockMvc.perform(post("/api/patients")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPatient_ShouldReturn200_WhenExists() throws Exception {
        Patient patient = createPatient(99999999000000L);

        mockMvc.perform(get("/api/patients/" + patient.getNationalId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nationalId").value(99999999000000L));
    }

    @Test
    void getPatient_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/patients/12121212121212")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePatient_ShouldReturn200_WhenValidRequest() throws Exception {
        createPatient(11100011000011L);

        PatientRequest request = new PatientRequest(
                "11100011000011", LocalDate.of(1995, 6, 20),
                "Female", "Giza", "Updated Name", "01011112233");

        mockMvc.perform(put("/api/patients/11100011000011")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void updatePatient_ShouldReturn404_WhenNotFound() throws Exception {
        PatientRequest request = new PatientRequest(
                "99988877000000", LocalDate.of(1990, 1, 1),
                "Male", "Cairo", "Nobody", "01000000000");

        mockMvc.perform(put("/api/patients/99988877000000")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPatients_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/patients")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void anyPatientEndpoint_ShouldReturn403_WhenRoleInsufficient() throws Exception {
        RegisterRequest reg = new RegisterRequest(
                "patient1", "patient1@test.com", "pass123", "Patient One");
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andReturn();
        String patientToken = objectMapper.readTree(
                regResult.getResponse().getContentAsString()).get("token").asText();

        mockMvc.perform(get("/api/patients")
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isForbidden());
    }
}