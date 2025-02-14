package com.example.elice_3rd.hospital.service;

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

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HospitalSearchService {

    private final HospitalQueryRepository hospitalQueryRepository;

    public Slice<HospitalResponse> findAllByCategoryIdV2(Long categoryId, Double latitude, Double longitude, Pageable pageable) {
        List<Tuple> results = hospitalQueryRepository.findAllByCategoryIdWithDiagnosisSubjectV2(categoryId, latitude, longitude, pageable);

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


        public Slice<HospitalResponse> findAllByCategoryIdV1(Long categoryId, Double latitude, Double longitude, Pageable pageable) {
        Slice<Hospital> hospitals = hospitalQueryRepository.findAllByCategoryIdWithDiagnosisSubjectV1(categoryId, pageable);

        List<HospitalResponse> hospitalResponses = hospitals.getContent().stream()
                .map(hospital -> {
                    double distance = calculateDistance(latitude, longitude, hospital.getLatitude(), hospital.getLongitude());
                    return new HospitalResponse(hospital, distance);
                })
                .sorted(Comparator.comparingDouble(HospitalResponse::getDistanceFromUser))
                .toList();

        return new SliceImpl<>(hospitalResponses, pageable, hospitals.hasNext());
    }



    private double calculateDistance(Double latUser, Double lonUser, Double latHospital, Double lonHospital) {
        double R = 6371; // 지구 반지름 (km)
        double dLat = Math.toRadians(latUser - latHospital);
        double dLon = Math.toRadians(lonUser - lonHospital);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latUser)) * Math.cos(Math.toRadians(latHospital))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 거리 (km)
    }


}
