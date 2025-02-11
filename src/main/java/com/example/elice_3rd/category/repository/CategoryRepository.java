package com.example.elice_3rd.category.repository;

import com.example.elice_3rd.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
