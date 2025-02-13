package com.example.elice_3rd.hospital.batch.config;

import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import com.example.elice_3rd.diagnosisSubject.service.DiagnosisSubjectService;
import com.example.elice_3rd.hospital.dto.HospitalDetails;
import com.example.elice_3rd.hospital.dto.HospitalInfo;
import com.example.elice_3rd.hospital.entity.Hospital;
import com.example.elice_3rd.hospital.service.HospitalService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HospitalBatchJobConfig {

    private final HospitalService hospitalService;
    private final DiagnosisSubjectService diagnosisSubjectService;
    private final EntityManager em;

    @Bean
    public Job hospitalBatchJob(JobRepository jobRepository,Step truncateHospitalTableStep, Step hospitalBatchStep, Step hospitalUpdateStep) {
        return new JobBuilder("hospitalBatchJob", jobRepository)
                .start(truncateHospitalTableStep)
                .next(hospitalBatchStep)
                .next(hospitalUpdateStep)
                .build();
    }

    @Bean
    public Step truncateHospitalTableStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("truncateHospitalTableStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    truncateHospitalTable();
                    return null;
                }, transactionManager)
                .build();
    }

    @Transactional
    public void truncateHospitalTable() {
        em.createNativeQuery("TRUNCATE TABLE hospital").executeUpdate();
    }

    @Bean
    public Step hospitalBatchStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("hospitalBatchStep", jobRepository)
                .<HospitalInfo, Hospital>chunk(1000, transactionManager) // 1000개 단위로 처리
                .reader(hospitalApiItemReader())
                .processor(hospitalApiItemProcessor())
                .writer(hospitalApiItemWriter())
                .build();
    }

    @Bean
    public Step hospitalUpdateStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("hospitalUpdateStep", jobRepository)
                .<HospitalDetails, HospitalDetails>chunk(1000, transactionManager) // HospitalDetails 그대로 넘긴다!
                .reader(hospitalDetailCsvReader())
                .writer(hospitalUpdateWriter())
                .build();
    }


    @Bean
    public ItemReader<HospitalInfo> hospitalApiItemReader() {
        return new ItemReader<>() {
            private final List<DiagnosisSubject> subjects = diagnosisSubjectService.findAll(); // 모든 진료과목 가져오기
            private int currentSubjectIndex = 0;
            private int currentPage = 1;
            private final int pageSize = 1000;
            private boolean isLastPage = false;
            private List<HospitalInfo> currentHospitalList = List.of(); // 현재 처리 중인 병원 리스트
            private int currentHospitalIndex = 0;

            @Override
            public HospitalInfo read() {
                // 현재 리스트에서 하나씩 반환
                if (currentHospitalIndex < currentHospitalList.size()) {
                    return currentHospitalList.get(currentHospitalIndex++);
                }

                // 새로운 데이터 요청
                if (isLastPage && currentSubjectIndex >= subjects.size()) {
                    return null; // 모든 데이터 처리가 끝나면 종료
                }

                if (isLastPage) {
                    // 다음 진료과목으로 넘어가기
                    currentSubjectIndex++;
                    currentPage = 1;
                    isLastPage = false;
                }

                if (currentSubjectIndex >= subjects.size()) {
                    return null; // 모든 진료과목을 처리했으면 종료
                }

                DiagnosisSubject currentSubject = subjects.get(currentSubjectIndex);
                currentHospitalList = hospitalService.getHospitalsFromAPI(currentSubject, currentPage, pageSize);
                currentHospitalIndex = 0;

                if (currentHospitalList.size() < pageSize) {
                    isLastPage = true; // 마지막 페이지 확인
                }

                currentPage++;

                return currentHospitalList.isEmpty() ? null : currentHospitalList.get(currentHospitalIndex++);
            }
        };
    }


    @Bean
    public ItemProcessor<HospitalInfo, Hospital> hospitalApiItemProcessor() {
        return Hospital::create;
    }

    @Bean
    public ItemWriter<Hospital> hospitalApiItemWriter() {
        return new JpaItemWriterBuilder<Hospital>()
                .entityManagerFactory(em.getEntityManagerFactory())
                .build();
    }

    @Bean
    public FlatFileItemReader<HospitalDetails> hospitalDetailCsvReader() {
        FlatFileItemReader<HospitalDetails> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("hospital_details.csv")); // 파일 위치
        reader.setLinesToSkip(1); // 헤더 건너뛰기

        DefaultLineMapper<HospitalDetails> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(
                "ykiho",
                "name",

                "sundayClinicInfo",
                "holidayClinicInfo",

                "hasNightEmergency",
                "emergencyContact",

                "weekdayLunchTime",
                "saturdayLunchTime",

                "sundayOpenTime",
                "sundayCloseTime",
                "mondayOpenTime",
                "mondayCloseTime",
                "tuesdayOpenTime",
                "tuesdayCloseTime",
                "wednesdayOpenTime",
                "wednesdayCloseTime",
                "thursdayOpenTime",
                "thursdayCloseTime",
                "fridayOpenTime",
                "fridayCloseTime",
                "saturdayOpenTime",
                "saturdayCloseTime"
                ); // CSV 헤더명과 일치
        lineMapper.setLineTokenizer(tokenizer);

        BeanWrapperFieldSetMapper<HospitalDetails> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(HospitalDetails.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);

        return reader;
    }

/*    @Bean
    public ItemProcessor<HospitalDetails, Hospital> hospitalUpdateProcessor() {
        return details -> {
            List<Hospital> hospitals = hospitalService.findByYkiho(details.getYkiho());

            if (hospitals.isEmpty()) {
                log.warn("ykiho가 존재하지 않아 해당 데이터는 저장하지 않습니다. ykiho: {}", details.getYkiho());
                return null;
            }

            // 상세 정보 업데이트
            Hospital hospital = hospitals.get(0);
            hospital.updateDetails(details);
            return hospital;
        };
    }*/

    @Bean
    public ItemWriter<HospitalDetails> hospitalUpdateWriter() {
        return detailsList -> {
            List<String> ykihoList = detailsList.getItems().stream()
                    .map(HospitalDetails::getYkiho)
                    .distinct()
                    .toList();

            List<Hospital> hospitals = hospitalService.findAllByYkihoIn(ykihoList);
            Map<String, List<Hospital>> hospitalMap = hospitals.stream()
                    .collect(Collectors.groupingBy(Hospital::getYkiho));

            int updatedCount = 0; // 업데이트 성공 건수

            for (HospitalDetails details : detailsList.getItems()) {
                List<Hospital> matchedHospitals = hospitalMap.get(details.getYkiho());
                if (matchedHospitals == null) {
                    log.warn("ykiho가 존재하지 않아 해당 데이터는 저장하지 않습니다. ykiho: {}", details.getYkiho());
                    continue;
                }

                for (Hospital hospital : matchedHospitals) {
                    hospital.updateDetails(details);
                    updatedCount++;
                }
            }

            log.info("병원 상세정보 업데이트 완료: {}건", updatedCount);
        };
    }



}
