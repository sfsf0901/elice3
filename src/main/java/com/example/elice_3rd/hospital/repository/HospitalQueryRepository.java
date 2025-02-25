package com.example.elice_3rd.hospital.repository;

import com.example.elice_3rd.hospital.dto.request.HospitalSearchWithEmergencyCondition;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.example.elice_3rd.category.entity.QCategory.*;
import static com.example.elice_3rd.hospital.entity.QHospital.*;
import static com.example.elice_3rd.hospital.entity.QHospitalCategory.*;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

@Repository
public class HospitalQueryRepository {

    private static final double LATITUDE_DELTA = 0.27;
    private static final double LONGITUDE_DELTA = 0.34;

    private final JPAQueryFactory queryFactory;

    public HospitalQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<Tuple> findAllByCategoryId(Long categoryId, Boolean isOpen, Boolean hasNightClinic, Boolean hasSundayAndHolidayClinic, Double latitude, Double longitude, Pageable pageable) {
        NumberTemplate<Double> distanceExpression = numberTemplate(Double.class,
                "ST_Distance_Sphere(point({0}, {1}), point({2}, {3}))",
                longitude, latitude, hospital.longitude, hospital.latitude);

        return queryFactory
                .select(hospital, distanceExpression)
                .from(hospitalCategory)
                .join(hospitalCategory.category, category)
                .join(hospitalCategory.hospital, hospital)
                .where(
                        categoryIdEq(categoryId),
                        isOpenEq(isOpen),
                        hasNightClinicEq(hasNightClinic),
                        hasSundayAndHolidayClinicEq(hasSundayAndHolidayClinic),
                        hospital.latitude.between(latitude - LATITUDE_DELTA, latitude + LATITUDE_DELTA),
                        hospital.longitude.between(longitude - LONGITUDE_DELTA, longitude + LONGITUDE_DELTA)
                )
                .orderBy(distanceExpression.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    public List<Tuple> findAllByHospitalName(String hospitalName, Boolean isOpen, Boolean hasNightClinic, Boolean hasSundayAndHolidayClinic, double latitude, double longitude, Pageable pageable) {
        NumberTemplate<Double> distanceExpression = numberTemplate(Double.class,
                "ST_Distance_Sphere(point({0}, {1}), point({2}, {3}))",
                longitude, latitude, hospital.longitude, hospital.latitude);

        return queryFactory
                .select(hospital, distanceExpression)
                .from(hospital)
                .where(
                        hospitalNameContains(hospitalName),
                        isOpenEq(isOpen),
                        hasNightClinicEq(hasNightClinic),
                        hasSundayAndHolidayClinicEq(hasSundayAndHolidayClinic),
                        hospital.latitude.between(latitude - LATITUDE_DELTA, latitude + LATITUDE_DELTA),
                        hospital.longitude.between(longitude - LONGITUDE_DELTA, longitude + LONGITUDE_DELTA)
                )
                .orderBy(distanceExpression.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    public List<Tuple> findAllWithEmergency(HospitalSearchWithEmergencyCondition condition, Pageable pageable) {
        NumberTemplate<Double> distanceExpression = numberTemplate(Double.class,
                "ST_Distance_Sphere(point({0}, {1}), point({2}, {3}))",
                condition.getLongitude(), condition.getLatitude(), hospital.longitude, hospital.latitude);

        return queryFactory
                .select(hospital, distanceExpression)
                .from(hospital)
                .where(
                        hasNightEmergencyEq(condition.isHasNightEmergency()),
                        hospital.latitude.between(condition.getLatitude() - LATITUDE_DELTA, condition.getLatitude() + LATITUDE_DELTA),
                        hospital.longitude.between(condition.getLongitude() - LONGITUDE_DELTA, condition.getLongitude() + LONGITUDE_DELTA)
                )
                .orderBy(distanceExpression.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }


    private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? hospitalCategory.category.id.eq(categoryId) : null;
    }

    private BooleanExpression hospitalNameContains(String keyword) {
        return StringUtils.hasText(keyword) ? hospital.hospitalName.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression hasNightEmergencyEq(boolean hasNightEmergency) {
        return hasNightEmergency ? hospital.hasNightEmergency.eq(true) : null;
    }

    private BooleanExpression isOpenEq(Boolean isOpen) {
        if (isOpen == null || isOpen == false) {
            return null;
        }
        // 현재 요일 및 시간 가져오기
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        StringPath openTimeField = null, closeTimeField = null;

        switch (today) {
            case MONDAY -> {
                openTimeField = hospital.mondayOpenTime;
                closeTimeField = hospital.mondayCloseTime;
            }
            case TUESDAY -> {
                openTimeField = hospital.tuesdayOpenTime;
                closeTimeField = hospital.tuesdayCloseTime;
            }
            case WEDNESDAY -> {
                openTimeField = hospital.wednesdayOpenTime;
                closeTimeField = hospital.wednesdayCloseTime;
            }
            case THURSDAY -> {
                openTimeField = hospital.thursdayOpenTime;
                closeTimeField = hospital.thursdayCloseTime;
            }
            case FRIDAY -> {
                openTimeField = hospital.fridayOpenTime;
                closeTimeField = hospital.fridayCloseTime;
            }
            case SATURDAY -> {
                openTimeField = hospital.saturdayOpenTime;
                closeTimeField = hospital.saturdayCloseTime;
            }
            case SUNDAY -> {
                openTimeField = hospital.sundayOpenTime;
                closeTimeField = hospital.sundayCloseTime;
            }
            default -> {
                return null;
            }
        }

        // 필드가 null이면 조건을 적용하지 않음
        if (openTimeField == null || closeTimeField == null) {
            return null;
        }

        // 현재 시간 (HHmm 형식, 예: 10:30 → 1030)
        int now = LocalTime.now().getHour() * 100 + LocalTime.now().getMinute();

        // 병원의 운영시간을 정수로 변환하여 비교 (QueryDSL)
        NumberExpression<Integer> openTimeInt = openTimeField.stringValue().castToNum(Integer.class);
        NumberExpression<Integer> closeTimeInt = closeTimeField.stringValue().castToNum(Integer.class);

        return openTimeInt.loe(now).and(closeTimeInt.gt(now));
    }

    private BooleanExpression hasNightClinicEq(Boolean hasNightClinic) {
        if (hasNightClinic == null) {
            return null;
        }
        return hasNightClinic ? hospital.hasNightClinic.eq(true) : null;
    }

    private BooleanExpression hasSundayAndHolidayClinicEq(Boolean hasSundayAndHolidayClinic) {
        if (hasSundayAndHolidayClinic == null) {
            return null;
        }
        return hasSundayAndHolidayClinic ? hospital.hasSundayAndHolidayClinic.eq(true) : null;
    }
}
