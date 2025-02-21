package com.example.elice_3rd.hospital.batch.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HospitalBatchJobConfig {

    private final EntityManager em;
    private final Step saveHospitalTempStep;
    private final Step updateHospitalTempDetailsStep;
    private final Step saveHospitalStep;
    private final Step saveHospitalDiagnosisSubjectStep;
    private final Step saveHospitalCategoryStep;
    private final Step deduplicateHospitalCategoryStep;


    @Bean
    public Job hospitalBatchJob(JobRepository jobRepository, Step truncateHospitalTablesStep) {
        return new JobBuilder("hospitalBatchJob", jobRepository)
                .start(truncateHospitalTablesStep)
                .next(saveHospitalTempStep)
                .next(updateHospitalTempDetailsStep)
                .next(saveHospitalStep)
                .next(saveHospitalDiagnosisSubjectStep)
                .next(saveHospitalCategoryStep)
                .next(deduplicateHospitalCategoryStep)
                .build();
    }

    @Bean
    public Step truncateHospitalTablesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("truncateHospitalTablesStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    truncateTables();
                    return null;
                }, transactionManager)
                .build();
    }

    @Transactional
    public void truncateTables() {
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS=0").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE hospital_diagnosis_subject").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE hospital_category").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE hospital").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE hospital_temp").executeUpdate();
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS=1").executeUpdate();
        log.info("hospital, hospital_category, hospital_diagnosis_subject, hospital_temp 테이블 초기화 완료");
    }

}
