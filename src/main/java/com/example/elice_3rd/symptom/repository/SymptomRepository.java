package com.example.elice_3rd.symptom.repository;

import com.example.elice_3rd.symptom.entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    Optional<Symptom> findByName(String symptomName);

    List<Symptom> findTop10ByOrderByIdAsc();
}
