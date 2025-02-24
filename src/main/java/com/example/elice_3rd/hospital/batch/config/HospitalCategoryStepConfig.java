package com.example.elice_3rd.hospital.batch.config;


import com.example.elice_3rd.hospital.batch.entity.HospitalTemp;
import com.example.elice_3rd.hospital.entity.Hospital;
import com.example.elice_3rd.hospital.entity.HospitalCategory;
import com.example.elice_3rd.hospital.service.HospitalService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;
    import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HospitalCategoryStepConfig {

    private final EntityManager em;
    private final HospitalService hospitalService;

    @Bean
    public Step saveHospitalCategoryStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("saveHospitalCategoryStep", jobRepository)
                .<HospitalTemp, HospitalCategory>chunk(1000, transactionManager)
                .reader(hospitalTempCategoryItemReader())
                .processor(hospitalTempToHospitalCategoryProcessor())
                .writer(hospitalCategoryItemWriter())
                .listener(new StepExecutionListenerSupport() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepExecution.getExecutionContext().putLong("startTime", System.currentTimeMillis());
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        long startTime = stepExecution.getExecutionContext().getLong("startTime");
                        long endTime = System.currentTimeMillis();
                        log.info("HospitalCategory 생성 완료 - 처리 시간: {} ms", (endTime - startTime));
                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }

    @Bean
    public JpaPagingItemReader<HospitalTemp> hospitalTempCategoryItemReader() {
        JpaPagingItemReader<HospitalTemp> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(em.getEntityManagerFactory());
        reader.setQueryString("SELECT t FROM HospitalTemp t");
        reader.setPageSize(3000); // 3000개씩 가져옴
        return reader;
    }

/*    @Bean
    public ItemProcessor<HospitalTemp, HospitalCategory> hospitalTempToHospitalCategoryProcessor() {
        return hospitalTemp -> {
            Optional<Hospital> hospitalOpt = hospitalService.findByYkiho(hospitalTemp.getYkiho());

            if (hospitalOpt.isEmpty()) {
                log.warn("########Hospital not found for ykiho: {}", hospitalTemp.getYkiho());
                return null;
            }

            Hospital hospital = hospitalOpt.get();
            Category category = hospitalTemp.getCategory();

            // 중복 체크 제거하고 무조건 생성
            return HospitalCategory.create(hospital, category);
        };
    }*/

    @Bean
    public ItemProcessor<HospitalTemp, HospitalCategory> hospitalTempToHospitalCategoryProcessor() {
        return new ItemProcessor<HospitalTemp, HospitalCategory>() {
            private Map<String, Hospital> hospitalMap;

            @Override
            public HospitalCategory process(HospitalTemp hospitalTemp) {
                // 최초 실행 시점에만 병원 맵 초기화 (Lazy Initialization)
                if (hospitalMap == null) {
                    hospitalMap = hospitalService.findAll().stream()
                            .collect(Collectors.toMap(Hospital::getYkiho, Function.identity()));
                    log.info("######## HospitalCategoryStep - Hospital Map 사이즈: {}", hospitalMap.size());
                }

                Hospital hospital = hospitalMap.get(hospitalTemp.getYkiho());
                if (hospital == null) {
                    return null;
                }

                return HospitalCategory.create(hospital, hospitalTemp.getCategory());
            }
        };
    }



    @Bean
    public ItemWriter<HospitalCategory> hospitalCategoryItemWriter() {
        return new JpaItemWriterBuilder<HospitalCategory>()
                .entityManagerFactory(em.getEntityManagerFactory())
                .build();
    }
}
