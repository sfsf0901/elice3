package com.example.elice_3rd.hospital.batch.config;


import com.example.elice_3rd.hospital.batch.entity.HospitalTemp;
import com.example.elice_3rd.hospital.entity.Hospital;
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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HospitalNormalizationStepConfig {

    private final EntityManager em;


    @Bean
    public Step saveHospitalStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("saveHospitalStep", jobRepository)
                .<HospitalTemp, Hospital>chunk(1000, transactionManager)
                .reader(hospitalTempDistinctItemReader())
                .processor(hospitalTempToHospitalProcessor())
                .writer(hospitalItemWriter())
                .listener(new StepExecutionListenerSupport() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepExecution.getExecutionContext().putLong("startTime", System.currentTimeMillis());
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        long startTime = stepExecution.getExecutionContext().getLong("startTime");
                        long endTime = System.currentTimeMillis();
                        log.info("########saveHospitalStep 완료 - 처리 시간: {} ms", (endTime - startTime));
                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }


    @Bean
    public ItemReader<HospitalTemp> hospitalTempDistinctItemReader() {
        return new JpaPagingItemReaderBuilder<HospitalTemp>()
                .name("hospitalTempDistinctItemReader")
                .entityManagerFactory(em.getEntityManagerFactory())
                .queryString("SELECT t FROM HospitalTemp t WHERE t.id IN (SELECT MIN(t2.id) FROM HospitalTemp t2 GROUP BY t2.ykiho) ORDER BY t.id")
                .pageSize(3000)
                .build();
    }

    @Bean
    public ItemProcessor<HospitalTemp, Hospital> hospitalTempToHospitalProcessor() {
        return Hospital::create;
    }

    @Bean
    public ItemWriter<Hospital> hospitalItemWriter() {
        return new JpaItemWriterBuilder<Hospital>()
                .entityManagerFactory(em.getEntityManagerFactory())
                .build();
    }
}
