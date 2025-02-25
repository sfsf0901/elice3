package com.example.elice_3rd.hospital.entity;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HospitalCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public static HospitalCategory create(Hospital hospital, Category category) {
        HospitalCategory hospitalCategory = new HospitalCategory();
        hospitalCategory.hospital = hospital;
        hospitalCategory.category = category;
        return hospitalCategory;
    }
}
