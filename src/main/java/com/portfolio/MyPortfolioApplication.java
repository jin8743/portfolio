package com.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MyPortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyPortfolioApplication.class, args);
    }

}
