package com.innowise.soundfilemicroservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

import static com.innowise.soundfilemicroservice.constant.YamlPropertyConstant.*;

@Slf4j
@Configuration
public class AwsConfig {

    @Value(AWS_ENDPOINT_URL_PROPERTY)
    private String awsEndpointUrl;

    @Value(AWS_SECRET_KEY_PROPERTY)
    private String awsSecretKey;

    @Value(AWS_ACCESS_KEY_PROPERTY)
    private String awsAccessKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .forcePathStyle(true)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(awsEndpointUrl))
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(awsEndpointUrl))
                .build();
    }
}
