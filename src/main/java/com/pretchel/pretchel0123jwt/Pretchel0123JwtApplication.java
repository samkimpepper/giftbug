package com.pretchel.pretchel0123jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.HiddenHttpMethodFilter;


@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class Pretchel0123JwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(Pretchel0123JwtApplication.class, args);
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

}
