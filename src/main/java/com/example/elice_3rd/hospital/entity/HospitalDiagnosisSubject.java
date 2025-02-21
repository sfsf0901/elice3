package com.example.elice_3rd.hospital.entity;

import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HospitalDiagnosisSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_diagnosis_subject_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_subject_id")
    private DiagnosisSubject diagnosisSubject;

    public static HospitalDiagnosisSubject create(Hospital hospital, DiagnosisSubject diagnosisSubject) {
        HospitalDiagnosisSubject hospitalDiagnosisSubject = new HospitalDiagnosisSubject();
        hospitalDiagnosisSubject.hospital = hospital;
        hospitalDiagnosisSubject.diagnosisSubject = diagnosisSubject;
        return hospitalDiagnosisSubject;
    }
}
