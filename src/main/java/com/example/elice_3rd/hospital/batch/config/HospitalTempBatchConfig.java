package com.example.elice_3rd.hospital.batch.config;

import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import com.example.elice_3rd.diagnosisSubject.service.DiagnosisSubjectService;
import com.example.elice_3rd.hospital.batch.entity.HospitalTemp;
import com.example.elice_3rd.hospital.batch.service.HospitalTempService;
import com.example.elice_3rd.hospital.batch.dto.HospitalInfo;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HospitalTempBatchConfig {

    private final HospitalTempService hospitalTempService;
    private final DiagnosisSubjectService diagnosisSubjectService;
    private final EntityManager em;

    @Bean
    public Step saveHospitalTempStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("saveHospitalTempStep", jobRepository)
                .<HospitalInfo, HospitalTemp>chunk(1000, transactionManager) // 1000개 단위로 처리
                .reader(hospitalApiReader())
                .processor(hospitalTempProcessor())
                .writer(hospitalTempWriter())
                .build();
    }

    @Bean
    public ItemReader<HospitalInfo> hospitalApiReader() {
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
                currentHospitalList = hospitalTempService.getHospitalsFromAPI(currentSubject, currentPage, pageSize);
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
    public ItemProcessor<HospitalInfo, HospitalTemp> hospitalTempProcessor() {
        return HospitalTemp::create;
    }

    @Bean
    public ItemWriter<HospitalTemp> hospitalTempWriter() {
        return new JpaItemWriterBuilder<HospitalTemp>()
                .entityManagerFactory(em.getEntityManagerFactory())
                .build();
    }
}
