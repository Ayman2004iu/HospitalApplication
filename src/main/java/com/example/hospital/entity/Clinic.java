package com.example.hospital.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "clinics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Clinic extends BaseEntity {
    private String name;
    private String description;
    private String location;

    @OneToMany(mappedBy = "clinic")
    private List<Doctor> doctors;
}
