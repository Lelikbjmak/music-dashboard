package com.innowise.camelcommon.camel;

import com.innowise.camelcommon.config.TestConfig;
import com.innowise.camelcommon.dto.UploadedFileDto;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.innowise.camelcommon.constant.CamelRouteConstant.DOWNLOAD_FILE_ROUTE;

@MockEndpoints
@Testcontainers
@CamelSpringBootTest
@ActiveProfiles(value = "camel")
@Import(value = TestConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DownloadFileCamelRouteTest {

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

    @EndpointInject("mock:file:{{local.storage.path}}")
    private MockEndpoint mockLocalStorageUploadEndpoint;

    @EndpointInject(value = "mock:aws2-s3:{{aws.s3.bucket-name}}")
    private MockEndpoint mockS3Endpoint;

    @Value(value = "${local.storage.path}")
    private String localStoragePath;

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

        // OR

        Path localStorageDirectory = Path.of(localStoragePath);

        Assertions.assertTrue(localStorageDirectory.toFile().exists());
        Assertions.assertTrue(localStorageDirectory.toFile().isDirectory());

        File[] fileArray = localStorageDirectory.toFile().listFiles();
        Assertions.assertNotNull(fileArray);

        for (File file : fileArray) {
            if (file.isFile())
                Assertions.assertTrue(file.delete());
        }
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(localStackContainer);
        Assertions.assertTrue(localStackContainer.isCreated());
        Assertions.assertTrue(localStackContainer.isRunning());
        Assertions.assertNotNull(producerTemplate);
        Assertions.assertNotNull(mockS3Endpoint);
        Assertions.assertNotNull(localStoragePath);
    }

    @Test
    @Order(2)
    void mustDownloadTextFileFromS3() throws InterruptedException, IOException {

        final String fileName = "testFile";
        final String fileBody = "Uploading file to S3...";

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(awsS3BucketName)
                        .key(fileName).build(),
                RequestBody.fromBytes(fileBody.getBytes()));

        UploadedFileDto uploadedAudioFileDto = new UploadedFileDto(fileName, null, "S3");

        mockS3Endpoint.expectedMessageCount(1);
        mockS3Endpoint.expectedHeaderReceived(AWS2S3Constants.KEY, fileName);

        InputStream content = producerTemplate.requestBody(DOWNLOAD_FILE_ROUTE,
                uploadedAudioFileDto, InputStream.class);

        mockS3Endpoint.assertIsSatisfied();

        Assertions.assertNotNull(content);
        Assertions.assertArrayEquals(fileBody.getBytes(), content.readAllBytes());
    }

    @Test
    @Order(3)
    void mustDownloadFileFileFromS3() throws InterruptedException, IOException {

        File uploadedFile = new File("../test-files/Jemi.mp3");

        Assertions.assertTrue(uploadedFile.exists());

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(awsS3BucketName)
                        .key(uploadedFile.getName()).build(),
                RequestBody.fromFile(uploadedFile));

        UploadedFileDto uploadedAudioFileDto = new UploadedFileDto(uploadedFile.getName(), null, "S3");

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

            InputStream receivedBodyFromS3 = producerTemplate.requestBody(DOWNLOAD_FILE_ROUTE,
                    uploadedAudioFileDto, InputStream.class);

            mockS3Endpoint.assertIsSatisfied();

            Assertions.assertNotNull(receivedBodyFromS3);
            Assertions.assertArrayEquals(expectedByteContent, receivedBodyFromS3.readAllBytes());
        }
    }

    @Test
    @Order(4)
    void mustDownloadFromLocalStorage() throws IOException {

        final String fileNameHeader = "fileName";

        Path sourceFile = Path.of("../test-files/Jemi.mp3");
        Path destinationPath = Path.of(localStoragePath, sourceFile.getFileName().toString());
        Files.copy(sourceFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);

        final String fileName = "Nirvana_-_Something_In_The-Way";
        final String fileDestinationInLocalStorage = localStoragePath + "/Jemi.mp3";

        UploadedFileDto uploadedAudioFileDto = new UploadedFileDto(fileName, fileDestinationInLocalStorage, "LOCAL");

        mockLocalStorageUploadEndpoint.expectedMessageCount(1);
        mockLocalStorageUploadEndpoint.expectedHeaderReceived(fileNameHeader, fileName);

        byte[] receivedBodyFromLocalStorage = producerTemplate.requestBody(DOWNLOAD_FILE_ROUTE,
                uploadedAudioFileDto, InputStream.class).readAllBytes();

        Assertions.assertNotNull(receivedBodyFromLocalStorage);

        Resource fileResource = new FileSystemResource(fileDestinationInLocalStorage);

        try (InputStream expectedContent = new FileInputStream(fileResource.getFile())) {
            byte[] expectedContentByteArray = expectedContent.readAllBytes();
            Assertions.assertArrayEquals(expectedContentByteArray, receivedBodyFromLocalStorage);
        }
    }
}