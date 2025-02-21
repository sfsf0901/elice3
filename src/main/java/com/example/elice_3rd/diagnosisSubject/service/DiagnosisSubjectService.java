package com.example.elice_3rd.diagnosisSubject.service;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.category.service.CategoryService;
import com.example.elice_3rd.diagnosisSubject.dto.request.CreateDiagnosisSubjectRequest;
import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import com.example.elice_3rd.diagnosisSubject.repository.DiagnosisSubjectRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiagnosisSubjectService {

    private final DiagnosisSubjectRepository diagnosisSubjectRepository;
    private final CategoryService categoryService;

    @PostConstruct
    public void initCategoriesAndDiagnosisSubjects() throws IOException {
        // 검색 카테고리 객체 만들기
        categoryService.initCategories();

        List<Category> categories = categoryService.findAll();
        HashMap<String, Category> categoryMap = new HashMap<String, Category>();
        for (Category category : categories) {
            categoryMap.put(category.getName(), category);
        }

        // 진료과목 객체 만들기
        if (diagnosisSubjectRepository.count() == 0) {
            Resource resource = new ClassPathResource("diagnosis_subject.csv");
            List<DiagnosisSubject> diagnosisSubjects = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        DiagnosisSubject diagnosisSubject = DiagnosisSubject.create(split[0], split[1], split[2]);
                        // 진료과목에 검색 카테고리 매핑하기
                        diagnosisSubject.updateCategory(categoryMap.get(diagnosisSubject.getCategoryName()));
                        return diagnosisSubject;
                    }).toList();
            diagnosisSubjectRepository.saveAll(diagnosisSubjects);
        }
    }

    public Long create(CreateDiagnosisSubjectRequest request) {
        //TODO 권한 확인 추가

        Category category = categoryService.findByCategoryId(request.getCategoryId());

        DiagnosisSubject diagnosisSubject = diagnosisSubjectRepository.save(DiagnosisSubject.create(request.getCode(), request.getName(), request.getCategoryName(), category));
        return diagnosisSubject.getId();
    }

    public DiagnosisSubject findDiagnosisSubject(Long diagnosisSubjectId) {
        return diagnosisSubjectRepository.findById(diagnosisSubjectId).orElseThrow(() -> new IllegalArgumentException("해당 진료과목은 존재하지 않습니다. diagnosisSubjectId: " + diagnosisSubjectId));
    }

    @Transactional(readOnly = true)
    public List<DiagnosisSubject> findAll() {
        return diagnosisSubjectRepository.findAll();
//        List<DiagnosisSubject> list = diagnosisSubjectRepository.findAll();
//        return list.size() > 2 ? list.subList(0, 2) : list;
    }

}
