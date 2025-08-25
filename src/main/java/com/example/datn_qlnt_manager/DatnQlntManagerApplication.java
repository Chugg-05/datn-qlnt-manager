package com.example.datn_qlnt_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.example.datn_qlnt_manager.configuration.OtpProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties(OtpProperties.class)
public class DatnQlntManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatnQlntManagerApplication.class, args);
    }
}
