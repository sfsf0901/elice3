package com.example.elice_3rd.category.service;

import com.example.elice_3rd.category.dto.request.CreateCategoryRequest;
import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void initCategories() {
        if (categoryRepository.count() == 0) {
            Resource resource = new ClassPathResource("categories.csv");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withQuote('"'));

                List<Category> categories = csvParser.getRecords().stream()
                        .map(record -> Category.create(record.get(0), record.get(1))) // 첫 번째 필드(진료과목), 두 번째 필드(설명)
                        .toList();

                categoryRepository.saveAll(categories);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("########카테고리 생성 실패");
            }
        }
    }

    public Long create(CreateCategoryRequest request) {
        //TODO 권한 확인 추가

        Category category = categoryRepository.save(Category.create(request.getName(), request.getDescription()));
        return category.getId();
    }

    public Long updateName(Long categoryId, CreateCategoryRequest request) {
        Category category = findByCategoryId(categoryId);
        category.updateName(request.getName());
        return category.getId();
    }

    public Long updateDescription(Long categoryId, CreateCategoryRequest request) {
        Category category = findByCategoryId(categoryId);
        category.updateDescription(request.getDescription());
        return category.getId();
    }

    public Long delete(Long categoryId) {
        Category category = findByCategoryId(categoryId);
        category.delete();
        return category.getId();
    }

    @Transactional(readOnly = true)
    public Category findByCategoryId(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("해당 카테고리는 존재하지 않습니다. categoryId: " + categoryId));
    }

    @Transactional(readOnly = true)
    public Category findByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

}
