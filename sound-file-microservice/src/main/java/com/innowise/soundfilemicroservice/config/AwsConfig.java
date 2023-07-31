package com.innowise.soundfilemicroservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

import static com.innowise.soundfilemicroservice.constant.YamlPropertyConstant.AWS_ENDPOINT_URL_PROPERTY;

@Slf4j
@Configuration
public class AwsConfig {

    @Value(AWS_ENDPOINT_URL_PROPERTY)
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
