package com.innowise.soundfilemicroservice.camel;

import com.innowise.soundfilemicroservice.util.Mp3TrackTitleParserUtil;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.innowise.soundfilemicroservice.constant.CamelConstant.UPLOAD_S3_ROUTE;

@MockEndpoints
@Testcontainers
@CamelSpringBootTest
@ActiveProfiles(value = "camel")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class S3CamelRouteTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.S3);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3));
    }

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Value(value = "${aws.s3.bucket-name}")
    private String awsS3BucketName;

    @EndpointInject("mock:aws2-s3:{{aws.s3.bucket-name}}")
    private MockEndpoint mockS3Endpoint;

    @BeforeEach
    void beforeTestCase() throws IOException, InterruptedException {
        mockS3Endpoint.reset();
        String createBucketCommand = "awslocal s3 mb s3://" + awsS3BucketName;
        localStackContainer.execInContainer("sh", "-c", createBucketCommand);
    }

    @AfterEach
    void afterTestCase() throws IOException, InterruptedException {
        String createBucketCommand = "awslocal s3 rb s3://" + awsS3BucketName + " --force";
        localStackContainer.execInContainer("sh", "-c", createBucketCommand);
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(localStackContainer);
        Assertions.assertTrue(localStackContainer.isRunning());
        Assertions.assertNotNull(producerTemplate);
        Assertions.assertNotNull(mockS3Endpoint);
    }

    @Test
    @Order(2)
    void mustUploadFileToS3() throws InterruptedException, IOException {

        final String fileNameHeader = "fileName";
        File uploadedFile = new File("../test-files/Jemi.mp3");

        Assertions.assertTrue(uploadedFile.exists());

        final String fileName = Mp3TrackTitleParserUtil.parseTrackTitle(new FileInputStream(uploadedFile));
        System.out.println(fileName);

        MultipartFile file = new MockMultipartFile(uploadedFile.getName(), fileName, null, new FileInputStream(uploadedFile));

        mockS3Endpoint.expectedMessageCount(1);
        mockS3Endpoint.expectedHeaderReceived(fileNameHeader, fileName);
        mockS3Endpoint.expectedHeaderReceived(AWS2S3Constants.KEY, fileName);

        producerTemplate.sendBody(UPLOAD_S3_ROUTE, file);

        ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(awsS3BucketName)
                .key(fileName)
                .build());

        try (InputStream uploadedContent = new FileInputStream(uploadedFile)) {
            Assertions.assertArrayEquals(responseBytes.asByteArray(), uploadedContent.readAllBytes());
        }

        mockS3Endpoint.assertIsSatisfied();
    }
}