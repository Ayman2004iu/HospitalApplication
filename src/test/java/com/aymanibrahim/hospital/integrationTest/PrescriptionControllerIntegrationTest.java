package com.aymanibrahim.hospital.integrationTest;

import com.aymanibrahim.hospital.dto.request.PrescriptionItemRequest;
import com.aymanibrahim.hospital.dto.request.PrescriptionRequest;
import com.aymanibrahim.hospital.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PrescriptionControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void createPrescription_ShouldReturn201_WhenValidAndDoctorRole() throws Exception {
        String doctorToken = createDoctorAndGetToken("presc.doc.new@hospital.com");

        Department dept = createDepartment("PrescDept-New");
        Clinic clinic = createClinic("PrescClinic-New");

        Doctor doctor = doctorRepository.findAll().stream()
                .filter(d -> d.getEmail().startsWith("presc.doc.new"))
                .findFirst().orElseThrow();

        Patient patient = createPatient(66600002L);

        Medication med = new Medication();
        med.setName("TestMed");
        med.setCode("MED-T-" + System.nanoTime());
        med.setUnitPrice(BigDecimal.valueOf(10));
        med.setQuantityAvailable(50);
        medicationRepository.save(med);

        Visit visit = createOpenVisit(patient, doctor, dept, clinic);

        PrescriptionItemRequest itemReq = new PrescriptionItemRequest();
        itemReq.setMedicationId(med.getId());
        itemReq.setDosage("1 tablet");
        itemReq.setFrequency("twice daily");
        itemReq.setDurationDays(7);
        itemReq.setQuantity(14);

        PrescriptionRequest request = new PrescriptionRequest(patient.getId(), visit.getId(), "Take after meals", List.of(itemReq));

        mockMvc.perform(post("/api/prescriptions")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllPrescriptions_ShouldReturn200_WhenAdmin() throws Exception {
        mockMvc.perform(get("/api/prescriptions")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}