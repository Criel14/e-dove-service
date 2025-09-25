package com.criel.edove.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.criel.edove")
public class EDoveGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EDoveGatewayApplication.class, args);
    }

}
