package com.example.webpos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableCaching
@Configuration
public class WebPosApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebPosApplication.class, args);
    }
}
