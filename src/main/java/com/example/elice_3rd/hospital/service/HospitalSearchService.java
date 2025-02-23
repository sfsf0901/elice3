package com.example.elice_3rd.hospital.service;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.category.service.CategoryService;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchByCategoryCondition;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchByKeywordCondition;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchWithEmergencyCondition;
import com.example.elice_3rd.hospital.dto.response.HospitalResponse;
import com.example.elice_3rd.hospital.entity.Hospital;
import com.example.elice_3rd.hospital.repository.HospitalQueryRepository;
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

    public Slice<HospitalResponse> findAllByCategoryId(HospitalSearchByCategoryCondition condition, Pageable pageable) {
        long startTime = System.currentTimeMillis();
        List<Tuple> results = hospitalQueryRepository.findAllByCategoryId(
                condition.getCategoryId(),
                condition.isHasNightClinic(),
                condition.isHasSundayAndHolidayClinic(),
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
                condition.isHasNightClinic(),
                condition.isHasSundayAndHolidayClinic(),
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
        String result = ollamaService.analyzeKeyword(condition.getKeyword());
        System.out.println("########result = " + result);
        
        List<Tuple> results = null;

        Category category = categoryService.findByName(condition.getKeyword());
        if (category != null) {
            results = hospitalQueryRepository.findAllByCategoryId(
                    category.getId(),
                    condition.getHasNightClinic(),
                    condition.getHasSundayAndHolidayClinic(),
                    condition.getLatitude(),
                    condition.getLongitude(),
                    pageable);
        } else if (result.startsWith("1")) {
            result = result.replace("1:", "").trim(); // 병원 이름만 남기기
            results = hospitalQueryRepository.findAllByCategoryId(
                    categoryService.findByName(result).getId(),
                    condition.getHasNightClinic(),
                    condition.getHasSundayAndHolidayClinic(),
                    condition.getLatitude(),
                    condition.getLongitude(),
                    pageable);
        } else if (result.startsWith("2")) {
            hospitalQueryRepository.findAllByHospitalName(
                    condition.getKeyword(),
                    condition.getHasNightClinic(),
                    condition.getHasSundayAndHolidayClinic(),
                    condition.getLatitude(),
                    condition.getLongitude(),
                    pageable);
        } else {
            // TODO 거리순으로 병원 검색하는 로직 추가
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
