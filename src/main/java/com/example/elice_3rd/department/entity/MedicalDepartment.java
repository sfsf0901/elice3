package com.example.elice_3rd.department.entity;

import com.example.elice_3rd.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDepartment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medical_department_id")
    private Long id;

    private int code;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}

