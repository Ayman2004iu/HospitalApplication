package com.example.hospital.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "departments")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Department extends BaseEntity {

    private String description;
    private String name;

    @OneToMany(mappedBy = "department")
    private List<Doctor> doctors;
}
