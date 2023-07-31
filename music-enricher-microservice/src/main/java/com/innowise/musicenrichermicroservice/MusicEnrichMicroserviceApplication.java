package com.innowise.musicenrichermicroservice;

import org.apache.camel.opentelemetry.starter.CamelOpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@CamelOpenTelemetry
@SpringBootApplication
@EnableDiscoveryClient
public class MusicEnrichMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicEnrichMicroserviceApplication.class, args);
	}

}
