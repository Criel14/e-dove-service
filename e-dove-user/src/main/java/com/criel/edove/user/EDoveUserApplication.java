package com.criel.edove.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.criel.edove")
public class EDoveUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(EDoveUserApplication.class, args);
    }

}
