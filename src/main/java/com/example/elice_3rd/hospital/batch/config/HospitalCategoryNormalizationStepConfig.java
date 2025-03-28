package com.example.elice_3rd.hospital.batch.config;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HospitalCategoryNormalizationStepConfig {

    private final EntityManager em;

    @Bean
    public Step deduplicateHospitalCategoryStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("deduplicateHospitalCategoryStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int deletedCount = em.createNativeQuery("""
                        DELETE t1
                        FROM hospital_category t1
                        JOIN hospital_category t2
                          ON t1.hospital_id = t2.hospital_id
                          AND t1.category_id = t2.category_id
                          AND t1.hospital_category_id > t2.hospital_category_id
                        """).executeUpdate();

                    log.info("중복 HospitalCategory 삭제 완료 - {}건 제거됨", deletedCount);
                    return null;
                }, transactionManager)
                .build();
    }
}
