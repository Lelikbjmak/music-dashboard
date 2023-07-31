package com.innowise.trackmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TrackMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackMicroserviceApplication.class, args);
    }

}
