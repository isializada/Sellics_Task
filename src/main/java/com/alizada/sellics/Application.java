package com.alizada.sellics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.alizada.sellics.controller", "com.alizada.sellics.service", "com.alizada.sellics.config" })
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}

