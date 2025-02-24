package com.example.elice_3rd.hospital.batch.config;

import com.example.elice_3rd.hospital.batch.entity.HospitalTemp;
import com.example.elice_3rd.hospital.batch.service.HospitalTempService;
import com.example.elice_3rd.hospital.batch.dto.HospitalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HospitalTempDetailsStepConfig {

    private final HospitalTempService hospitalTempService;

    @Bean
    public Step updateHospitalTempDetailsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("updateHospitalTempDetailsStep", jobRepository)
                .<HospitalDetails, HospitalDetails>chunk(1000, transactionManager) // HospitalDetails 그대로 넘긴다!
                .reader(hospitalDetailCsvFileReader())
                .writer(hospitalTempDetailsWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<HospitalDetails> hospitalDetailCsvFileReader() {
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
    public ItemWriter<HospitalDetails> hospitalTempDetailsWriter() {
        return detailsList -> {
            List<String> ykihoList = detailsList.getItems().stream()
                    .map(HospitalDetails::getYkiho)
                    .distinct()
                    .toList();

            List<HospitalTemp> hospitals = hospitalTempService.findAllByYkihoIn(ykihoList);
            Map<String, List<HospitalTemp>> hospitalMap = hospitals.stream()
                    .collect(Collectors.groupingBy(HospitalTemp::getYkiho));

            int updatedCount = 0; // 업데이트 성공 건수

            for (HospitalDetails details : detailsList.getItems()) {
                List<HospitalTemp> matchedHospitals = hospitalMap.get(details.getYkiho());
                if (matchedHospitals == null) {
                    log.warn("ykiho가 존재하지 않아 해당 데이터는 저장하지 않습니다. ykiho: {}", details.getYkiho());
                    continue;
                }

                for (HospitalTemp hospitalTemp : matchedHospitals) {
                    hospitalTemp.updateDetails(details);
                    updatedCount++;
                }
            }

            log.info("병원 상세정보 업데이트 완료: {}건", updatedCount);
        };
    }
}
