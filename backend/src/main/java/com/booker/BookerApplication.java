package com.booker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookerApplication.class, args);
    }
}