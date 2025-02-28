package com.example.elice_3rd.hospital.repository;

import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import com.example.elice_3rd.hospital.entity.Hospital;
import com.example.elice_3rd.hospital.entity.HospitalDiagnosisSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HospitalDiagnosisSubjectRepository extends JpaRepository<HospitalDiagnosisSubject, Long> {

    @Query("SELECT d.name FROM HospitalDiagnosisSubject hds JOIN hds.diagnosisSubject d WHERE hds.hospital = :hospital")
    List<String> findDiagnosisSubjectNamesByHospital(Hospital hospital);

}
