package com.innowise.soundfilemicroservice.controller;

import com.innowise.jwtcommontest.security.TestUserDetails;
import com.innowise.jwtcommontest.util.TestJwtTokenUtil;
import com.innowise.soundfilemicroservice.dto.UploadedAudioFileDto;
import com.innowise.soundfilemicroservice.util.Mp3TrackTitleParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Testcontainers
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(value = "integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SoundFileControllerIntegrationTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.S3);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3));
    }

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String awsS3BucketName;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(testRestTemplate);
    }

    @BeforeEach
    void beforeTestCase() throws IOException, InterruptedException {
        String createBucketCommand = "awslocal s3 mb s3://" + awsS3BucketName;
        localStackContainer.execInContainer("sh", "-c", createBucketCommand);
    }

    @AfterEach
    void afterTestCase() throws IOException, InterruptedException {
        String createBucketCommand = "awslocal s3 rb s3://" + awsS3BucketName + " --force";
        localStackContainer.execInContainer("sh", "-c", createBucketCommand);
    }

    @Test
    @Sql(value = "/sql/02-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenValidInput_thenUploadFileMustReturn200() {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = getHttpEntityWithUploadedFile(Set.of("ROLE_USER"));

        ResponseEntity<UploadedAudioFileDto> response = testRestTemplate
                .postForEntity("/api/v1/sound-file", requestEntity, UploadedAudioFileDto.class);
        UploadedAudioFileDto actualUploadedFile = response.getBody();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(actualUploadedFile);
    }

    @Test
    void whenNotAuthenticated_thenUploadFileMustReturn401() {

        ResponseEntity<String> response = testRestTemplate
                .exchange("/api/v1/sound-file", HttpMethod.POST, HttpEntity.EMPTY, String.class);
        String message = response.getBody();

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNotNull(message);
        Assertions.assertTrue(message.contains("Full authentication"));
    }

    @Test
    @Sql(value = "/sql/02-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenValidInput_thenDownloadFileMustReturn200() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = getHttpEntityWithUploadedFile(Set.of("ROLE_USER"));
        ResponseEntity<UploadedAudioFileDto> uploadedFileResponse = testRestTemplate.postForEntity("/api/v1/sound-file", requestEntity, UploadedAudioFileDto.class);

        Assertions.assertNotNull(uploadedFileResponse.getBody());
        long id = uploadedFileResponse.getBody().id();
        final String fileName = Mp3TrackTitleParserUtil.parseTrackTitle(new FileInputStream("../test-files/Jemi.mp3"));

        HttpEntity<?> downloadFileEntity = getHttEntityWithBearerAuth(Set.of("ROLE_USER"));
        ResponseEntity<byte[]> response = testRestTemplate
                .exchange("/api/v1/sound-file/{id}", HttpMethod.GET, downloadFileEntity, byte[].class, id);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getHeaders().containsKey(HttpHeaders.CONTENT_TYPE));
        Assertions.assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        Assertions.assertEquals(ContentDisposition.attachment().filename(fileName).build(), response.getHeaders().getContentDisposition());

        byte[] actualContentByteArray = response.getBody();

        Assertions.assertNotNull(actualContentByteArray);
        Resource fileResource = new FileSystemResource("../test-files/Jemi.mp3");

        try (InputStream expectedContent = new FileInputStream(fileResource.getFile())) {
            byte[] expectedContentByteArray = expectedContent.readAllBytes();
            Assertions.assertArrayEquals(expectedContentByteArray, actualContentByteArray);
        }
    }

    @Test
    void whenFileNotExists_thenDownloadFileMustReturn404() {

        HttpEntity<?> downloadFileEntity = getHttEntityWithBearerAuth(Set.of("ROLE_USER"));
        ResponseEntity<String> response = testRestTemplate
                .exchange("/api/v1/sound-file/{id}", HttpMethod.GET, downloadFileEntity, String.class, 1);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void whenNotAuthenticated_thenDownloadFileMustReturn401() {
        ResponseEntity<String> response = testRestTemplate
                .exchange("/api/v1/sound-file/{id}", HttpMethod.GET, HttpEntity.EMPTY, String.class, 1);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        String message = response.getBody();

        Assertions.assertNotNull(message);
        Assertions.assertTrue(message.contains("Full authentication"));
    }

    @Test
    @Sql(value = "/sql/02-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenAwsServiceUnavailable_thenDownloadFileMustReturn503() {
        HttpEntity<MultiValueMap<String, Object>> uploadFileRequest = getHttpEntityWithUploadedFile(Set.of("ROLE_USER"));
        testRestTemplate.postForEntity("/api/v1/sound-file", uploadFileRequest, UploadedAudioFileDto.class);

        emptyBucket();
        deleteBucket();

        HttpEntity<?> downloadFileRequest = getHttEntityWithBearerAuth(Set.of("ROLE_USER"));
        ResponseEntity<String> response = testRestTemplate
                .exchange("/api/v1/sound-file/{id}", HttpMethod.GET, downloadFileRequest, String.class, 1);

        Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());

        createBucket();
    }

    private HttpEntity<?> getHttEntityWithBearerAuth(Set<String> userRoles) {
        final String jwt = TestJwtTokenUtil.generateToken(new TestUserDetails(userRoles));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);

        return new HttpEntity<>(null, headers);
    }

    private HttpEntity<MultiValueMap<String, Object>> getHttpEntityWithUploadedFile(Set<String> userRoles) {
        final String jwt = TestJwtTokenUtil.generateToken(new TestUserDetails(userRoles));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        String fileName = "../test-files/Jemi.mp3";
        Resource fileResource = new FileSystemResource(fileName);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        return new HttpEntity<>(body, headers);
    }

    private void emptyBucket() {
        try {
            ListObjectsRequest listRequest = ListObjectsRequest.builder()
                    .bucket(awsS3BucketName).build();

            ListObjectsResponse listResponse = s3Client.listObjects(listRequest);
            List<S3Object> listObjects = listResponse.contents();

            List<ObjectIdentifier> objectsToDelete = new ArrayList<>();

            for (S3Object s3Object : listObjects) {
                objectsToDelete.add(ObjectIdentifier.builder().key(s3Object.key()).build());
            }

            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(awsS3BucketName)
                    .delete(Delete.builder().objects(objectsToDelete).build())
                    .build();

            s3Client.deleteObjects(deleteObjectsRequest);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    private void deleteBucket() {
        try {
            s3Client.deleteBucket(DeleteBucketRequest.builder()
                    .bucket(awsS3BucketName)
                    .build());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    private void createBucket() {
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(awsS3BucketName)
                .build());
    }
}