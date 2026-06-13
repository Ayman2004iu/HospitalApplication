package com.aymanibrahim.hospital.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import java.util.List;

@Entity
@Table(name = "clinics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE clinics SET is_deleted = true WHERE id = ?")
public class Clinic extends BaseEntity {
    private String name;
    private String description;
    private String location;

    @OneToMany(mappedBy = "clinic")
    private List<Doctor> doctors;
}