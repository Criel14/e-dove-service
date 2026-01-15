package com.criel.edove.assistant;

import com.criel.edove.feign.config.OpenFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(value = "com.criel.edove.feign", defaultConfiguration = OpenFeignConfig.class)
public class EDoveAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(EDoveAssistantApplication.class, args);
    }

}
