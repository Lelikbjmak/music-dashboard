package com.innowise.camelcommon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class TestConfig {

    @Value(value = "${aws.endpoint-url}")
    private String awsEndpointUrl;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .forcePathStyle(true)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(awsEndpointUrl))
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(awsEndpointUrl))
                .build();
    }

}
