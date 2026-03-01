package com.example.confectionery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ConfectioneryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfectioneryApplication.class, args);
    }

}
