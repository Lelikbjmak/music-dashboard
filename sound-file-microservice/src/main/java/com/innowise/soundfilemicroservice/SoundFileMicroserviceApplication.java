package com.innowise.soundfilemicroservice;

import org.apache.camel.opentelemetry.starter.CamelOpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@CamelOpenTelemetry
@SpringBootApplication
@EnableDiscoveryClient
public class SoundFileMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoundFileMicroserviceApplication.class, args);
    }

}
