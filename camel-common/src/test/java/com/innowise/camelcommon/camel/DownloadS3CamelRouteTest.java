package com.innowise.camelcommon.camel;

import com.innowise.camelcommon.config.TestConfig;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.aws2.s3.AWS2S3Operations;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.innowise.camelcommon.constant.CamelRouteConstant.DOWNLOAD_FROM_S3_ROUTE;

@MockEndpoints
@Testcontainers
@CamelSpringBootTest
@ActiveProfiles(value = "camel")
@Import(value = TestConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DownloadS3CamelRouteTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.S3);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3));
    }

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private S3Client s3Client;

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
    @Order(3)
    void mustDownloadTextFromS3() throws InterruptedException, IOException {

        final String fileName = "testFile";
        final String fileNameHeader = "fileName";
        final String fileBody = "Uploading file to S3...";

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(awsS3BucketName)
                        .key(fileName).build(),
                RequestBody.fromBytes(fileBody.getBytes()));

        mockS3Endpoint.expectedMessageCount(1);
        mockS3Endpoint.expectedHeaderReceived(AWS2S3Constants.KEY, fileName);
        mockS3Endpoint.expectedHeaderReceived(AWS2S3Constants.S3_OPERATION, AWS2S3Operations.getObject);

        InputStream receivedBodyFromS3 = producerTemplate.requestBodyAndHeader(DOWNLOAD_FROM_S3_ROUTE, null, fileNameHeader, fileName, InputStream.class);

        mockS3Endpoint.assertIsSatisfied();

        Assertions.assertNotNull(receivedBodyFromS3);
        Assertions.assertArrayEquals(fileBody.getBytes(), receivedBodyFromS3.readAllBytes());
    }

    @Test
    @Order(4)
    void mustDownloadFileFromS3() throws InterruptedException, IOException {

        final String fileNameHeader = "fileName";
        File uploadedFile = new File("../test-files/Jemi.mp3");

        Assertions.assertTrue(uploadedFile.exists());

        try (InputStream expectedContent = new FileInputStream(uploadedFile)) {

            byte[] expectedByteContent = expectedContent.readAllBytes();

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(awsS3BucketName)
                            .key(uploadedFile.getName()).build(),
                    RequestBody.fromBytes(expectedByteContent));

            mockS3Endpoint.expectedMessageCount(1);
            mockS3Endpoint.expectedHeaderReceived(AWS2S3Constants.KEY, uploadedFile.getName());
            mockS3Endpoint.expectedHeaderReceived(AWS2S3Constants.S3_OPERATION, AWS2S3Operations.getObject);

            InputStream receivedBodyFromS3 = producerTemplate.requestBodyAndHeader(DOWNLOAD_FROM_S3_ROUTE, null,
                    fileNameHeader, uploadedFile.getName(), InputStream.class);

            mockS3Endpoint.assertIsSatisfied();

            Assertions.assertNotNull(receivedBodyFromS3);
            Assertions.assertArrayEquals(expectedByteContent, receivedBodyFromS3.readAllBytes());
        }
    }
}