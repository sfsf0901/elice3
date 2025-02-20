package com.example.elice_3rd.category.repository;

import com.example.elice_3rd.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 상담 작성 및 조회에 이용
    Optional<Category> findByName(String name);
}
