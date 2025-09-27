package com.criel.edove.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.criel.edove")
@EnableFeignClients("com.criel.edove.feign")
public class EDoveAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(EDoveAuthApplication.class, args);
    }

}
