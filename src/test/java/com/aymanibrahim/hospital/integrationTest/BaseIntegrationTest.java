package com.aymanibrahim.hospital.integrationTest;

import com.aymanibrahim.hospital.dto.request.LoginRequest;
import com.aymanibrahim.hospital.entity.*;
import com.aymanibrahim.hospital.enums.PaymentStatus;
import com.aymanibrahim.hospital.enums.VisitStatus;
import com.aymanibrahim.hospital.repository.*;
import com.aymanibrahim.hospital.enums.RoleName;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected UserRepository userRepository;
    @Autowired protected RoleRepository roleRepository;
    @Autowired protected PatientRepository patientRepository;
    @Autowired protected DoctorRepository doctorRepository;
    @Autowired protected DepartmentRepository departmentRepository;
    @Autowired protected ClinicRepository clinicRepository;
    @Autowired protected VisitRepository visitRepository;
    @Autowired protected InvoiceRepository invoiceRepository;
    @Autowired protected MedicationRepository medicationRepository;
    @Autowired protected PrescriptionRepository prescriptionRepository;
    @Autowired protected PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL}")
    protected String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    protected String adminPassword;

    protected String adminToken;

    @BeforeEach
    void setUpBase() throws Exception {
        adminToken = loginAndGetToken(adminEmail, adminPassword);
    }

    protected String loginAndGetToken(String email, String password) throws Exception {
        LoginRequest req = new LoginRequest(email, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token")
                .asText();
    }

    protected Patient createPatient(Long nationalId) {
        Patient p = new Patient();
        p.setNationalId(String.valueOf(nationalId));
        p.setName("Test Patient");
        p.setDob(LocalDate.of(1990, 1, 1));
        p.setGender("Male");
        p.setAddress("Cairo");
        p.setPhone("01012345678");
        return patientRepository.save(p);
    }

    protected Department createDepartment(String name) {
        Department d = new Department();
        d.setName(name);
        d.setDescription("Test department");
        return departmentRepository.save(d);
    }

    protected Clinic createClinic(String name) {
        Clinic c = new Clinic();
        c.setName(name);
        c.setLocation("Floor 1");
        return clinicRepository.save(c);
    }

    protected Doctor createDoctor(String email, Department dept, Clinic clinic) {
        Role role = roleRepository.findByName(RoleName.ROLE_DOCTOR)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(RoleName.ROLE_DOCTOR);
                    return roleRepository.save(r);
                });

        User user = User.builder()
                .username(email)
                .email(email)
                .fullName("Dr Test")
                .password(passwordEncoder.encode("pass123"))
                .roles(new HashSet<>(Set.of(role)))
                .build();

        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setName("Dr Test");
        doctor.setEmail(email);
        doctor.setPhone("01012345678");
        doctor.setSpecialization("General");
        doctor.setLicenseNumber("LIC-IT-001");
        doctor.setDepartment(dept);
        doctor.setClinic(clinic);

        return doctorRepository.save(doctor);
    }

    protected String createDoctorAndGetToken(String email) throws Exception {
        Department dept = createDepartment("DocDept-" + System.nanoTime());
        Clinic clinic = createClinic("DocClinic-" + System.nanoTime());

        createDoctor(email, dept, clinic);

        return loginAndGetToken(email, "pass123");
    }

    protected Visit createOpenVisit(
            Patient patient,
            Doctor doctor,
            Department dept,
            Clinic clinic) {

        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setDoctor(doctor);
        visit.setDepartment(dept);
        visit.setClinic(clinic);
        visit.setStatus(VisitStatus.OPEN);
        visit.setVisitDate(LocalDateTime.now());

        Invoice invoice = new Invoice();
        invoice.setVisit(visit);
        invoice.setPatient(patient);
        invoice.setTotal(BigDecimal.valueOf(150));
        invoice.setPaymentStatus(PaymentStatus.UNPAID);
        invoice.setLines(new ArrayList<>());

        visit.setInvoice(invoice);

        return visitRepository.save(visit);
    }

    protected Invoice createInvoiceForVisit(Patient patient, BigDecimal total) {
        Department dept = createDepartment("InvoiceDept-" + System.nanoTime());
        Clinic clinic = createClinic("InvoiceClinic-" + System.nanoTime());
        Doctor doctor = createDoctor("invoice.doc." + System.nanoTime() + "@hospital.com", dept, clinic);

        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setDoctor(doctor);
        visit.setDepartment(dept);
        visit.setClinic(clinic);
        visit.setStatus(VisitStatus.OPEN);
        visit.setVisitDate(LocalDateTime.now());

        Invoice invoice = new Invoice();
        invoice.setVisit(visit);
        invoice.setPatient(patient);
        invoice.setTotal(total);
        invoice.setPaymentStatus(PaymentStatus.UNPAID);

        visit.setInvoice(invoice);

        visitRepository.save(visit);

        return invoiceRepository.findByVisit_Id(visit.getId())
                .orElseThrow();
    }

    protected Medication createMedication(
            String name,
            String code,
            int qty,
            BigDecimal price) {

        Medication med = new Medication();
        med.setName(name);
        med.setCode(code);
        med.setQuantityAvailable(qty);
        med.setUnitPrice(price);

        return medicationRepository.save(med);
    }

    protected Prescription createPrescription(String notes) {
        Prescription p = new Prescription();
        p.setNotes(notes);
        p.setItems(new ArrayList<>());

        return prescriptionRepository.save(p);
    }
}