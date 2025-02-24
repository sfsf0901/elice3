package com.example.elice_3rd.symptom.repository;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.symptom.entity.Symptom;
import com.example.elice_3rd.symptom.entity.SymptomCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SymptomCategoryRepository extends JpaRepository<SymptomCategory, Long> {
    Optional<SymptomCategory> findBySymptomAndCategory(Symptom symptom, Category category);
}
