package com.example.elice_3rd.hospital.service;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.category.service.CategoryService;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchByCategoryCondition;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchByKeywordCondition;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchWithEmergencyCondition;
import com.example.elice_3rd.hospital.dto.response.HospitalResponse;
import com.example.elice_3rd.hospital.entity.Hospital;
import com.example.elice_3rd.hospital.repository.HospitalQueryRepository;
import com.example.elice_3rd.symptom.entity.Symptom;
import com.example.elice_3rd.symptom.service.SymptomCategoryService;
import com.example.elice_3rd.symptom.service.SymptomService;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HospitalSearchService {

    private final HospitalQueryRepository hospitalQueryRepository;
    private final OllamaService ollamaService;
    private final CategoryService categoryService;
    private final SymptomService symptomService;
    private final SymptomCategoryService symptomCategoryService;

    public Slice<HospitalResponse> findAllByCategoryId(HospitalSearchByCategoryCondition condition, Pageable pageable) {
        long startTime = System.currentTimeMillis();
        List<Tuple> results = hospitalQueryRepository.findAllByCategoryId(
                condition.getCategoryId(),
                condition.getIsOpen(),
                condition.getHasNightClinic(),
                condition.getHasSundayAndHolidayClinic(),
                condition.getLatitude(),
                condition.getLongitude(),
                pageable);
        long endTime = System.currentTimeMillis();
        System.out.println("쿼리 실행 시간(ms): " + (endTime - startTime));

        return getHospitalResponses(pageable, results);
    }

    public Slice<HospitalResponse> findAllByHospitalName(HospitalSearchByCategoryCondition condition, Pageable pageable) {
        long startTime = System.currentTimeMillis();
        List<Tuple> results = hospitalQueryRepository.findAllByHospitalName(
                condition.getHospitalName(),
                condition.getIsOpen(),
                condition.getHasNightClinic(),
                condition.getHasSundayAndHolidayClinic(),
                condition.getLatitude(),
                condition.getLongitude(),
                pageable);
        long endTime = System.currentTimeMillis();
        System.out.println("쿼리 실행 시간(ms): " + (endTime - startTime));

        return getHospitalResponses(pageable, results);
    }

    public Slice<HospitalResponse> findAllWithEmergency(HospitalSearchWithEmergencyCondition condition, Pageable pageable) {
        long startTime = System.currentTimeMillis();
        List<Tuple> results = hospitalQueryRepository.findAllWithEmergency(condition, pageable);
        long endTime = System.currentTimeMillis();
        System.out.println("쿼리 실행 시간(ms): " + (endTime - startTime));

        return getHospitalResponses(pageable, results);
    }

    public Slice<HospitalResponse> findAllByKeyword(HospitalSearchByKeywordCondition condition, Pageable pageable) {
        List<Tuple> results = null;

        // 1. 키워드를 기반으로 카테고리 또는 증상 찾기
        Category category = categoryService.findByName(condition.getKeyword());
        Symptom symptom = symptomService.findByName(condition.getKeyword());

        // 2. 카테고리 검색이 가능하면 바로 병원 조회
        if (category != null) {
            results = hospitalQueryRepository.findAllByCategoryId(
                    category.getId(),
                    condition.getIsOpen(),
                    condition.getHasNightClinic(),
                    condition.getHasSundayAndHolidayClinic(),
                    condition.getLatitude(),
                    condition.getLongitude(),
                    pageable);
        }
        // 3. 증상 검색이 가능하면 우선순위가 가장 높은 카테고리를 찾고 병원 조회
        else if (symptom != null) {
            Category findCategory = symptomCategoryService.findCategoryBySymptomWithHighestPriority(symptom);
            results = hospitalQueryRepository.findAllByCategoryId(
                    findCategory.getId(),
                    condition.getIsOpen(),
                    condition.getHasNightClinic(),
                    condition.getHasSundayAndHolidayClinic(),
                    condition.getLatitude(),
                    condition.getLongitude(),
                    pageable);
        }
        // 4. 위 두 경우가 아니라면, 키워드 분석을 실행
        else {
            String analyzedKeyword = ollamaService.analyzeKeyword(condition.getKeyword());
            System.out.println("########analyzedKeyword = " + analyzedKeyword);

            if (analyzedKeyword.startsWith("1")) {
                analyzedKeyword = analyzedKeyword.replace("1:", "").trim(); // 병원 이름만 남기기
                Category analyzedCategory = categoryService.findByName(analyzedKeyword);
                if (analyzedCategory != null) {
                    // 증상 & 증상_카테고리 등록 비동기 처리
                    symptomCategoryService.saveSymptomAndCategoryAsync(condition.getKeyword(), analyzedCategory);

                    results = hospitalQueryRepository.findAllByCategoryId(
                            analyzedCategory.getId(),
                            condition.getIsOpen(),
                            condition.getHasNightClinic(),
                            condition.getHasSundayAndHolidayClinic(),
                            condition.getLatitude(),
                            condition.getLongitude(),
                            pageable);
                }
            } else if (analyzedKeyword.startsWith("2")) {
                results = hospitalQueryRepository.findAllByHospitalName(
                        condition.getKeyword(),
                        condition.getIsOpen(),
                        condition.getHasNightClinic(),
                        condition.getHasSundayAndHolidayClinic(),
                        condition.getLatitude(),
                        condition.getLongitude(),
                        pageable);
            } else {
                // TODO 거리순으로 병원 검색하는 로직 추가
                return null;
            }
        }

        return getHospitalResponses(pageable, results);
    }


    private static SliceImpl<HospitalResponse> getHospitalResponses(Pageable pageable, List<Tuple> results) {
        List<HospitalResponse> hospitalResponses = results.stream()
                .map(tuple -> {
                    Hospital hospital = tuple.get(0, Hospital.class);
                    Double distance = tuple.get(1, Double.class);
                    return new HospitalResponse(hospital, distance);
                })
                .toList();

        boolean hasNext = hospitalResponses.size() > pageable.getPageSize();

        List<HospitalResponse> finalContent = hasNext
                ? hospitalResponses.subList(0, pageable.getPageSize())
                : hospitalResponses;

        return new SliceImpl<>(finalContent, pageable, hasNext);
    }
}
