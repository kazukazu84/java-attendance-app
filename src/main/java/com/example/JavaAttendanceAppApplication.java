package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.adminshift.service.ShiftApplicationEventService;

@SpringBootApplication
public class JavaAttendanceAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaAttendanceAppApplication.class, args);
    }

    /**
     * 起動時に設定テーブルの初期データを作成
     */
    @Bean
    CommandLineRunner initSetting(
            ShiftApplicationEventService service) {

        return args -> {
            service.initializeSetting();
        };
    }

}