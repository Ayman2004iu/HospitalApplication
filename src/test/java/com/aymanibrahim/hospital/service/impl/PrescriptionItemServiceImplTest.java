package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.PrescriptionItemRequest;
import com.aymanibrahim.hospital.dto.response.PrescriptionItemResponse;
import com.aymanibrahim.hospital.entity.Medication;
import com.aymanibrahim.hospital.entity.Prescription;
import com.aymanibrahim.hospital.entity.PrescriptionItem;
import com.aymanibrahim.hospital.enums.PrescriptionItemStatus;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.mapper.PrescriptionItemMapper;
import com.aymanibrahim.hospital.repository.MedicationRepository;
import com.aymanibrahim.hospital.repository.PrescriptionItemRepository;
import com.aymanibrahim.hospital.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrescriptionItemServiceImplTest {

    @Mock private PrescriptionItemRepository itemRepository;
    @Mock private PrescriptionRepository prescriptionRepository;
    @Mock private MedicationRepository medicationRepository;
    @Mock private PrescriptionItemMapper itemMapper;

    @InjectMocks
    private PrescriptionItemServiceImpl prescriptionItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Medication buildMedication(int quantity, BigDecimal unitPrice) {
        Medication med = new Medication();
        med.setQuantityAvailable(quantity);
        med.setUnitPrice(unitPrice);
        return med;
    }

    private PrescriptionItemRequest buildRequest(Long prescriptionId, Long medicationId, int quantity) {
        PrescriptionItemRequest req = new PrescriptionItemRequest();
        req.setPrescriptionId(prescriptionId);
        req.setMedicationId(medicationId);
        req.setQuantity(quantity);
        req.setDosage("1 tablet");
        req.setFrequency("twice daily");
        req.setDurationDays(7);
        return req;
    }

    @Test
    void createItemFromRequest_ShouldReturnPending_WhenStockSufficient() {
        Medication med = buildMedication(20, BigDecimal.valueOf(10));
        PrescriptionItemRequest request = buildRequest(1L, 1L, 5);

        when(medicationRepository.findById(1L)).thenReturn(Optional.of(med));

        PrescriptionItem item = prescriptionItemService.buildItem(new Prescription(), request);

        assertEquals(PrescriptionItemStatus.PENDING, item.getStatus());
        assertEquals(BigDecimal.valueOf(50), item.getPrice());
    }

    @Test
    void createItemFromRequest_ShouldReturnOutOfStock_WhenInsufficientStock() {
        Medication med = buildMedication(3, BigDecimal.valueOf(10));
        PrescriptionItemRequest request = buildRequest(1L, 1L, 10);

        when(medicationRepository.findById(1L)).thenReturn(Optional.of(med));

        PrescriptionItem item = prescriptionItemService.buildItem(new Prescription(), request);

        assertEquals(PrescriptionItemStatus.OUT_OF_STOCK, item.getStatus());
    }

    @Test
    void createItemFromRequest_ShouldReturnOutOfStock_WhenQuantityIsNull() {
        Medication med = new Medication();
        med.setQuantityAvailable(null);
        med.setUnitPrice(BigDecimal.valueOf(10));

        PrescriptionItemRequest request = buildRequest(1L, 1L, 5);

        when(medicationRepository.findById(1L)).thenReturn(Optional.of(med));

        PrescriptionItem item = prescriptionItemService.buildItem(new Prescription(), request);

        assertEquals(PrescriptionItemStatus.OUT_OF_STOCK, item.getStatus());
    }

    @Test
    void createItemFromRequest_ShouldCalculatePriceCorrectly() {
        Medication med = buildMedication(50, BigDecimal.valueOf(15));
        PrescriptionItemRequest request = buildRequest(1L, 1L, 4);

        when(medicationRepository.findById(1L)).thenReturn(Optional.of(med));

        PrescriptionItem item = prescriptionItemService.buildItem(new Prescription(), request);

        assertEquals(BigDecimal.valueOf(60), item.getPrice());
    }

    @Test
    void createItemFromRequest_ShouldThrow_WhenMedicationNotFound() {
        PrescriptionItemRequest request = buildRequest(1L, 99L, 5);
        when(medicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionItemService.buildItem(new Prescription(), request));
    }

    @Test
    void addPrescriptionItem_ShouldAdd_WhenValidRequest() {
        Prescription prescription = new Prescription();
        Medication med = buildMedication(10, BigDecimal.valueOf(5));
        PrescriptionItemRequest request = buildRequest(1L, 1L, 3);

        PrescriptionItem saved = new PrescriptionItem();

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(med));
        when(itemRepository.save(any(PrescriptionItem.class))).thenReturn(saved);
        when(itemMapper.toResponse(saved)).thenReturn(new PrescriptionItemResponse());

        PrescriptionItemResponse result = prescriptionItemService.addPrescriptionItem(request);

        assertNotNull(result);
        verify(itemRepository).save(any(PrescriptionItem.class));
    }

    @Test
    void addPrescriptionItem_ShouldThrow_WhenPrescriptionNotFound() {
        PrescriptionItemRequest request = buildRequest(99L, 1L, 3);
        when(prescriptionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionItemService.addPrescriptionItem(request));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getPrescriptionItemById_ShouldReturn_WhenExists() {
        PrescriptionItem item = new PrescriptionItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toResponse(item)).thenReturn(new PrescriptionItemResponse());

        assertNotNull(prescriptionItemService.getPrescriptionItemById(1L));
    }

    @Test
    void getPrescriptionItemById_ShouldThrow_WhenNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionItemService.getPrescriptionItemById(99L));
    }

    @Test
    void getAllPrescriptionItems_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(itemRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(new PrescriptionItem())));
        when(itemMapper.toResponse(any())).thenReturn(new PrescriptionItemResponse());

        assertEquals(1, prescriptionItemService.getAllPrescriptionItems(pageable).getTotalElements());
    }

    @Test
    void updatePrescriptionItem_ShouldUpdate_WhenValidRequest() {
        PrescriptionItem item = new PrescriptionItem();
        item.setDosage("old dosage");
        item.setFrequency("once daily");
        item.setDurationDays(3);

        Medication med = buildMedication(20, BigDecimal.valueOf(8));
        PrescriptionItemRequest request = buildRequest(1L, 1L, 6);
        request.setDosage("2 tablets");
        request.setFrequency("three times daily");
        request.setDurationDays(14);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(med));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(new PrescriptionItemResponse());

        prescriptionItemService.updatePrescriptionItem(1L, request);

        assertEquals(6, item.getQuantity());
        assertEquals(BigDecimal.valueOf(48), item.getPrice());
        assertEquals("2 tablets", item.getDosage());
        assertEquals("three times daily", item.getFrequency());
        assertEquals(14, item.getDurationDays());
        verify(itemRepository).save(item);
    }

    @Test
    void updatePrescriptionItem_ShouldThrow_WhenItemNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionItemService.updatePrescriptionItem(99L, buildRequest(1L, 1L, 5)));
    }

    @Test
    void updatePrescriptionItem_ShouldThrow_WhenMedicationNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(new PrescriptionItem()));
        when(medicationRepository.findById(99L)).thenReturn(Optional.empty());

        PrescriptionItemRequest request = buildRequest(1L, 99L, 5);
        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionItemService.updatePrescriptionItem(1L, request));
    }

    @Test
    void updatePrescriptionItem_ShouldNotUpdateQuantityOrPrice_WhenQuantityIsNull() {
        PrescriptionItem item = new PrescriptionItem();
        item.setQuantity(5);
        item.setPrice(BigDecimal.valueOf(50));

        Medication med = buildMedication(20, BigDecimal.valueOf(10));
        PrescriptionItemRequest request = buildRequest(1L, 1L, 5);
        request.setQuantity(null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(med));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(new PrescriptionItemResponse());

        prescriptionItemService.updatePrescriptionItem(1L, request);

        assertEquals(5, item.getQuantity());
        assertEquals(BigDecimal.valueOf(50), item.getPrice());
    }
}