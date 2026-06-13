package com.aymanibrahim.hospital.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"national_id", "deleted_token"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE patients SET is_deleted = true, deleted_token = id WHERE id = ?")
public class Patient extends BaseEntity {

    @Column(name = "national_id", nullable = false, length = 14)
    private String nationalId;

    private LocalDate dob;

    private String gender;

    private String address;

    private String name;

    private String phone;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Visit> visits;

    @Column(name = "deleted_token", nullable = false)
    @Builder.Default
    private Long deletedToken = 0L;
}