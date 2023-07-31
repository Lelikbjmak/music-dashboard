package com.innowise.soundfilemicroservice.camel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.camelcommon.dto.UploadedFileDto;
import com.innowise.soundfilemicroservice.constant.CamelConstant;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:aws2-sqs:{{aws.sqs.queue-name[0]}}")
    private MockEndpoint mockSqsEndpoint;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void resetMockS3Endpoint() {
        mockSqsEndpoint.reset();
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(localStackContainer);
        Assertions.assertTrue(localStackContainer.isRunning());
        Assertions.assertNotNull(producerTemplate);
        Assertions.assertNotNull(mockSqsEndpoint);
        Assertions.assertNotNull(objectMapper);
    }

    @Test
    @Order(2)
    void sendMessageToSQSTest() throws InterruptedException, JsonProcessingException {

        UploadedFileDto uploadedFileSQSDto = new UploadedFileDto("file", "s3/file", "S3");

        mockSqsEndpoint.expectedMessageCount(1);
        mockSqsEndpoint.expectedBodiesReceived(objectMapper.writeValueAsString(uploadedFileSQSDto));

        producerTemplate.sendBody(CamelConstant.UPLOAD_TO_SQS_ROUTE, uploadedFileSQSDto);

        mockSqsEndpoint.assertIsSatisfied();
    }
}