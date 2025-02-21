package com.example.elice_3rd.hospital.repository;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.hospital.entity.Hospital;
import com.example.elice_3rd.hospital.entity.HospitalCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalCategoryRepository extends JpaRepository<HospitalCategory, Long> {
    boolean existsByHospitalAndCategory(Hospital hospital, Category category);
}
