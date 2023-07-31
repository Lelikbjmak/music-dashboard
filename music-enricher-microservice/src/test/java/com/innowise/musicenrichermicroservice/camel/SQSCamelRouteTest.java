package com.innowise.musicenrichermicroservice.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.io.IOException;
import java.net.URI;

import static com.innowise.musicenrichermicroservice.constant.CamelConstant.UPLOAD_TO_SQS_ROUTE;

@MockEndpoints
@Testcontainers
@CamelSpringBootTest
@ActiveProfiles(value = "camel")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SQSCamelRouteTest {
    
    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
    }

    @Autowired
    private SqsClient sqsClient;

    @Value(value = "music-file-queue")
    private String downloadSQSQueueName;

    private CamelContext context;

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:aws2-sqs:{{aws.sqs.queue-name[1]}}")
    private MockEndpoint mockUploadSQSEndpoint;

    @EndpointInject("mock:aws2-sqs:music-file-queue")
    private MockEndpoint mockDownloadSQSEndpoint;

    @BeforeEach
    void sqsSetupBefore() throws IOException, InterruptedException {
        localStackContainer.execInContainer("sh", "-c", "awslocal sqs create-queue --queue-name music-data-test-queue");
        localStackContainer.execInContainer("sh", "-c", "awslocal sqs create-queue --queue-name music-file-test-queue");
        mockUploadSQSEndpoint.reset();
        mockDownloadSQSEndpoint.reset();
    }

    @AfterEach
    void sqsSetupAfter() throws IOException, InterruptedException {
        localStackContainer.execInContainer("sh", "-c", "awslocal sqs create-queue --queue-name music-data-test-queue");
        localStackContainer.execInContainer("sh", "-c", "awslocal sqs create-queue --queue-name music-file-test-queue");
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(producerTemplate);
        Assertions.assertNotNull(mockUploadSQSEndpoint);
        Assertions.assertTrue(localStackContainer.isRunning());
    }

    @Test
    void mustUploadToSqs() throws InterruptedException {
        String message = "Test message...";
        mockUploadSQSEndpoint.expectedBodiesReceived(message);
        producerTemplate.sendBody(UPLOAD_TO_SQS_ROUTE, message);
        mockUploadSQSEndpoint.assertIsSatisfied();
    }

    @Test
    void mustDownloadFromSQS() throws JsonProcessingException, InterruptedException {

        String json = "{\"fileName\":\"testFile\", \"path\":\"S3/testFile\", \"storage\":\"S3\"}";

        URI SQSUri = localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS);

        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(SQSUri + "/000000000000/" + downloadSQSQueueName)
                .messageBody(json)
                .build());
    }
}