package com.example.confectionery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = { org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
@EnableJpaAuditing
@EnableAsync

public class ConfectioneryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfectioneryApplication.class, args);
    }

}
