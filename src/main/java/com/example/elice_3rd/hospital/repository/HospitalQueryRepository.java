package com.example.elice_3rd.hospital.repository;

import com.example.elice_3rd.hospital.dto.request.HospitalSearchByCategoryCondition;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchByKeywordCondition;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchWithEmergencyCondition;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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

    public List<Tuple> findAllByCategoryId(Long categoryId, boolean hasNightClinic, boolean hasSundayAndHolidayClinic, Double latitude, Double longitude, Pageable pageable) {
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

    public List<Tuple> findAllByHospitalName(String hospitalName, boolean hasNightClinic, boolean hasSundayAndHolidayClinic, double latitude, double longitude, Pageable pageable) {
        NumberTemplate<Double> distanceExpression = numberTemplate(Double.class,
                "ST_Distance_Sphere(point({0}, {1}), point({2}, {3}))",
                longitude, latitude, hospital.longitude, hospital.latitude);

        return queryFactory
                .select(hospital, distanceExpression)
                .from(hospital)
                .where(
                        hospitalNameContains(hospitalName),
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

    private BooleanExpression hasNightClinicEq(boolean hasNightClinic) {
        return hasNightClinic ? hospitalCategory.hospital.hasNightClinic.eq(true) : null;
    }

    private BooleanExpression hasSundayAndHolidayClinicEq(boolean hasSundayAndHolidayClinic) {
        return hasSundayAndHolidayClinic ? hospitalCategory.hospital.hasSundayAndHolidayClinic.eq(true) : null;
    }
}
