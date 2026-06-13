package com.aymanibrahim.hospital.entity;

import com.aymanibrahim.hospital.enums.PrescriptionItemStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;

@Entity
@Table(name = "prescription_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE prescription_items SET is_deleted = true WHERE id = ?")
public class PrescriptionItem extends BaseEntity {


    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    private String dosage;

    private String frequency;
    private Integer  durationDays;
    private Integer  quantity;

    @Enumerated(EnumType.STRING)
    private PrescriptionItemStatus status = PrescriptionItemStatus.PENDING;

    private BigDecimal price;
}