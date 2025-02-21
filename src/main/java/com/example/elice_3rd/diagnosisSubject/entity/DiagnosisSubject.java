package com.example.elice_3rd.diagnosisSubject.entity;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisSubject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_subject_id")
    private Long id;

    private String diagnosisSubjectCode;

    @Column(nullable = false, unique = true)
    private String name;

    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


    public static DiagnosisSubject create(String diagnosisSubjectCode, String name, String categoryName, Category category) {
        DiagnosisSubject diagnosisSubject = new DiagnosisSubject();
        diagnosisSubject.diagnosisSubjectCode = diagnosisSubjectCode;
        diagnosisSubject.name = name;
        diagnosisSubject.category = category;
        return diagnosisSubject;
    }

    public static DiagnosisSubject create(String diagnosisSubjectCode, String name, String categoryName) {
        DiagnosisSubject diagnosisSubject = new DiagnosisSubject();
        diagnosisSubject.diagnosisSubjectCode = diagnosisSubjectCode;
        diagnosisSubject.name = name;
        diagnosisSubject.categoryName = categoryName;
        return diagnosisSubject;
    }

    public void updateDiagnosisSubjectCode(String diagnosisSubjectCode) {
        this.diagnosisSubjectCode = diagnosisSubjectCode;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }
}

