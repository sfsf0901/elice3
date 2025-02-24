package com.example.elice_3rd.hospital.batch.config;

import com.example.elice_3rd.hospital.batch.entity.HospitalTemp;
import com.example.elice_3rd.hospital.entity.Hospital;
import com.example.elice_3rd.hospital.entity.HospitalDiagnosisSubject;
import com.example.elice_3rd.hospital.service.HospitalService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HospitalDiagnosisSubjectStepConfig {

    private final EntityManager em;
    private final HospitalService hospitalService;

    @Bean
    public Step saveHospitalDiagnosisSubjectStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("saveHospitalDiagnosisSubjectStep", jobRepository)
                .<HospitalTemp, HospitalDiagnosisSubject>chunk(1000, transactionManager)
                .reader(hospitalTempItemReader())
                .processor(hospitalTempToHospitalDiagnosisSubjectProcessor())
                .writer(hospitalDiagnosisSubjectItemWriter())
                .listener(new StepExecutionListenerSupport() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        log.info("HospitalDiagnosisSubjectStep 시작");
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        log.info("HospitalDiagnosisSubjectStep 완료");
                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }

    @Bean
    public JpaPagingItemReader<HospitalTemp> hospitalTempItemReader() {
        return new JpaPagingItemReaderBuilder<HospitalTemp>()
                .name("hospitalTempItemReader")
                .entityManagerFactory(em.getEntityManagerFactory())
                .queryString("SELECT t FROM HospitalTemp t ORDER BY t.id")
                .pageSize(3000)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<HospitalTemp, HospitalDiagnosisSubject> hospitalTempToHospitalDiagnosisSubjectProcessor() {
        return new ItemProcessor<HospitalTemp, HospitalDiagnosisSubject>() {
            private Map<String, Hospital> hospitalMap;

            @Override
            public HospitalDiagnosisSubject process(HospitalTemp hospitalTemp) {
                if (hospitalMap == null) {  // 초기화 시점 지연 (Lazy Initialization)
                    hospitalMap = hospitalService.findAll().stream()
                            .collect(Collectors.toMap(Hospital::getYkiho, Function.identity()));
                    log.info("######## HospitalDiagnosisSubjectStep - Hospital Map 사이즈: {}", hospitalMap.size());
                }

                Hospital hospital = hospitalMap.get(hospitalTemp.getYkiho());
                if (hospital == null) {
                    return null;
                }

                return HospitalDiagnosisSubject.create(hospital, hospitalTemp.getDiagnosisSubject());
            }
        };
    }

    @Bean
    public ItemWriter<HospitalDiagnosisSubject> hospitalDiagnosisSubjectItemWriter() {
        return new JpaItemWriterBuilder<HospitalDiagnosisSubject>()
                .entityManagerFactory(em.getEntityManagerFactory())
                .build();
    }
}
