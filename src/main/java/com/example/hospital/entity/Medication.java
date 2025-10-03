package com.example.hospital.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "medications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Medication extends BaseEntity {
    private String name;
    private String code;
    private Integer quantityAvailable;
    private BigDecimal unitPrice;
}

