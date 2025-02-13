package com.example.elice_3rd.hospital.batch;

import com.example.elice_3rd.hospital.repository.HospitalRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HospitalBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job hospitalBatchJob;
    private final HospitalRepository hospitalRepository;

    /**
     * 5월, 8월, 11월, 2월의 1일 자정(00:00:00)에 실행
     */
    @Scheduled(cron = "0 0 0 1 5,8,11,2 *")
//    @Scheduled(cron = "* 37 * * * *")
    public void runHospitalBatchJob() {
        try {
            log.info("#####병원 데이터 배치 작업 시작");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // 매 실행마다 다른 파라미터 추가
                    .toJobParameters();

            jobLauncher.run(hospitalBatchJob, jobParameters);

            log.info("#####병원 데이터 배치 작업 완료");
        } catch (Exception e) {
            log.error("#####병원 데이터 배치 작업 실패", e);
        }
    }

//    @PostConstruct
    public void initHospitalBatchJob() {
        if (hospitalRepository.count() == 0) {
            log.info("#####병원 테이블 비어 있음. 배치 작업 실행 시작");
            try {
                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters();

                jobLauncher.run(hospitalBatchJob, jobParameters);
            } catch (Exception e) {
                log.error("#####병원 배치 작업 실행 중 오류 발생", e);
            }
        } else {
            log.info("#####병원 테이블 데이터 존재. 배치 작업 실행 안 함");
        }
    }
}
