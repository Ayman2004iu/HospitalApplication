package com.aymanibrahim.hospital.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "departments")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE departments SET is_deleted = true WHERE id = ?")
public class Department extends BaseEntity {

    private String description;
    private String name;

    @OneToMany(mappedBy = "department")
    private List<Doctor> doctors;
}