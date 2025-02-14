package com.example.elice_3rd.hospital.repository;

import com.example.elice_3rd.hospital.entity.Hospital;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.elice_3rd.diagnosisSubject.entity.QDiagnosisSubject.*;
import static com.example.elice_3rd.hospital.entity.QHospital.*;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

@Repository
public class HospitalQueryRepository {

    private static final double LATITUDE_DELTA = 0.27;
    private static final double LONGITUDE_DELTA = 0.34;

    private final JPAQueryFactory queryFactory;

    public HospitalQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<Tuple> findAllByCategoryIdWithDiagnosisSubjectV2(Long categoryId, Double latitude, Double longitude, Pageable pageable) {
        NumberTemplate<Double> distanceExpression = numberTemplate(Double.class,
                "ST_Distance_Sphere(point({0}, {1}), point({2}, {3}))",
                longitude, latitude, hospital.longitude, hospital.latitude);

        return queryFactory
                .select(hospital, distanceExpression)
                .from(hospital)
                .join(hospital.diagnosisSubject, diagnosisSubject).fetchJoin()
                .where(
                        hospital.category.id.eq(categoryId),
                        hospital.latitude.between(latitude - LATITUDE_DELTA, latitude + LATITUDE_DELTA),
                        hospital.longitude.between(longitude - LONGITUDE_DELTA, longitude + LONGITUDE_DELTA)
                )
                .orderBy(distanceExpression.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    public Slice<Hospital> findAllByCategoryIdWithDiagnosisSubjectV1(Long categoryId, Pageable pageable) {
        List<Hospital> content = queryFactory
                .selectFrom(hospital)
                .join(hospital.diagnosisSubject, diagnosisSubject).fetchJoin()
                .where(hospital.category.id.eq(categoryId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // 페이지 사이즈보다 1개 더 가져와서, 다음 페이지 여부 확인
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();

        if (hasNext) {
            content.remove(content.size() - 1); // 다음 페이지 여부 확인용 데이터 삭제
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    public Slice<Hospital> findAllByCategoryId(Long categoryId, Pageable pageable) {
        List<Hospital> content = queryFactory
                .selectFrom(hospital)
                .where(hospital.category.id.eq(categoryId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();

        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

}
