package com.esb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.esb.mapper")
@EnableAsync
public class ESBApplication {
    public static void main(String[] args) {
        SpringApplication.run(ESBApplication.class, args);
    }
}