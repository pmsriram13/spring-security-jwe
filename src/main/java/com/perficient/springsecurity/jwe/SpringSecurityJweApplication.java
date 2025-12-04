package com.perficient.springsecurity.jwe;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.perficient.springsecurity.jwe","com.perficient.predictor.batch.*"})
@EnableJpaRepositories(basePackages = "com.perficient.predictor.batch.repository")
@EntityScan(basePackages = "com.perficient.predictor.batch.entity")
@EnableScheduling
public class SpringSecurityJweApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityJweApplication.class, args);
    }

}
