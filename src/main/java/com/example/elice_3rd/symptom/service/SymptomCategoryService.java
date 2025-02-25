package com.example.elice_3rd.symptom.service;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.category.repository.CategoryRepository;
import com.example.elice_3rd.category.service.CategoryService;
import com.example.elice_3rd.diagnosisSubject.service.DiagnosisSubjectService;
import com.example.elice_3rd.symptom.entity.Symptom;
import com.example.elice_3rd.symptom.entity.SymptomCategory;
import com.example.elice_3rd.symptom.repository.SymptomCategoryQueryRepository;
import com.example.elice_3rd.symptom.repository.SymptomCategoryRepository;
import com.example.elice_3rd.symptom.repository.SymptomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SymptomCategoryService {

    private final SymptomCategoryRepository symptomCategoryRepository;
    private final SymptomRepository symptomRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final DiagnosisSubjectService diagnosisSubjectService;
    private final SymptomCategoryQueryRepository symptomCategoryQueryRepository;

    private final ReentrantLock lock = new ReentrantLock(); // 락 객체 생성

    @PostConstruct
    public void initSymptomAndSymptomCategory() {
        // 검색 카테고리 객체 만들기
        categoryService.initCategories();
        // 진료과목 객체 만들기
        diagnosisSubjectService.initDiagnosisSubjects();

        if (symptomCategoryRepository.count() == 0) {
            Resource resource = new ClassPathResource("symptom_category.csv");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                List<SymptomCategory> symptomCategories = reader.lines()
                        .skip(1) // 첫 번째 줄(헤더) 건너뛰기
                        .map(line -> {
                            String[] parts = line.split(",");
                            String symptomName = parts[0];
                            String categoryName = parts[1];
                            Integer priority = Integer.parseInt(parts[2]);

                            // Symptom 존재 여부 확인 후 없으면 생성
                            Symptom symptom = symptomRepository.findByName(symptomName)
                                    .orElseGet(() -> symptomRepository.save(Symptom.create(symptomName)));

                            // Category 조회 (존재하지 않으면 예외 발생)
                            Category category = categoryRepository.findByName(categoryName)
                                    .orElse(null);

                            // SymptomCategory 생성
                            return SymptomCategory.create(symptom, category, priority, symptomName);
                        })
                        .toList();

                symptomCategoryRepository.saveAll(symptomCategories);
                log.info("########증상 및 증상_카테고리 데이터 초기화 완료: {}개", symptomCategories.size());

            } catch (Exception e) {
                log.error("########증상 데이터 초기화 실패", e);
            }
        }
    }

    @Transactional(readOnly = true)
    public Category findCategoryBySymptomWithHighestPriority(Symptom symptom) {
        return symptomCategoryQueryRepository.findCategoryBySymptomWithHighestPriority(symptom);
    }

    @Async
    @Transactional
    public void saveSymptomAndCategoryAsync(String symptomName, Category category) {
        log.info("########비동기 실행 시작 - Thread: {}", Thread.currentThread().getName());

        lock.lock(); // 락 획득
        try {
            // 새로운 증상 저장 (기존에 없을 경우에만)
            Symptom symptom = symptomRepository.findByName(symptomName).orElse(null);

            if (symptom == null) {
                symptom = Symptom.create(symptomName);
                symptomRepository.save(symptom);
                SymptomCategory createdSymptomCategory = SymptomCategory.create(symptom, category, 2, symptom.getName());
                symptomCategoryRepository.save(createdSymptomCategory);
            } else {
                // 기존에 등록된 관계가 없으면 저장
                SymptomCategory symptomCategory = symptomCategoryRepository.findBySymptomAndCategory(symptom, category).orElse(null);
                if (symptomCategory == null) {
                    SymptomCategory createdSymptomCategory = SymptomCategory.create(symptom, category, 2, symptom.getName());
                    symptomCategoryRepository.save(createdSymptomCategory);
                }
            }
        } catch (Exception e) {
            log.error("########비동기 작업 중 예외 발생", e);
        } finally {
            lock.unlock(); // 락 해제
        }

        log.info("########비동기 실행 완료 - Thread: {}", Thread.currentThread().getName());
    }
}
