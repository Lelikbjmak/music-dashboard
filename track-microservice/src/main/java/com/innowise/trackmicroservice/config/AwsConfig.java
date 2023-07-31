package com.innowise.trackmicroservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

import static com.innowise.trackmicroservice.constant.YamlPropertyConstant.AWS_ENDPOINT_URL_PROPERTY;

@Configuration
public class AwsConfig {

    @Value(value = AWS_ENDPOINT_URL_PROPERTY)
    private String awsEndpointUrl;

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(awsEndpointUrl))
                .build();
    }
}
