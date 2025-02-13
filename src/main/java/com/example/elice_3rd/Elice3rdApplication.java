package com.example.elice_3rd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 스케줄링 활성화
@EnableJpaAuditing
@SpringBootApplication
public class Elice3rdApplication {

    public static void main(String[] args) {
        SpringApplication.run(Elice3rdApplication.class, args);
    }

}
