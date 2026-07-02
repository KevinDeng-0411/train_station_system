package com.trainstation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.trainstation.mapper")
public class TrainStationApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainStationApplication.class, args);
    }
}
