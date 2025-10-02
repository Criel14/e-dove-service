package com.criel.edove.store;

import com.criel.edove.feign.config.OpenFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.criel.edove")
@EnableFeignClients(value = "com.criel.edove.feign", defaultConfiguration = OpenFeignConfig.class)
public class EDoveStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(EDoveStoreApplication.class, args);
    }

}
