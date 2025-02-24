package com.example.elice_3rd.symptom.repository;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.category.entity.QCategory;
import com.example.elice_3rd.symptom.entity.QSymptomCategory;
import com.example.elice_3rd.symptom.entity.Symptom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import static com.example.elice_3rd.category.entity.QCategory.*;
import static com.example.elice_3rd.symptom.entity.QSymptomCategory.*;

@Repository
public class SymptomCategoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    public SymptomCategoryQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    public Category findCategoryBySymptomWithHighestPriority(Symptom symptom) {
        Category categoryWithPriorityOne = queryFactory
                .select(category)
                .from(symptomCategory)
                .join(symptomCategory.category, category)
                .where(
                        symptomCategory.symptom.eq(symptom),
                        symptomCategory.priority.eq(1) // 우선순위 1인 것 우선 검색
                )
                .fetchOne(); // 단일 결과 반환

        if (categoryWithPriorityOne != null) {
            return categoryWithPriorityOne; // 우선순위 1인 게 있으면 반환
        }

        // 없으면 그냥 아무거나 하나 반환 (우선순위 상관없이)
        return queryFactory
                .select(category)
                .from(symptomCategory)
                .join(symptomCategory.category, category)
                .where(symptomCategory.symptom.eq(symptom))
                .fetchFirst(); // 첫 번째 값 반환 (우선순위 상관 없음)
    }
}
