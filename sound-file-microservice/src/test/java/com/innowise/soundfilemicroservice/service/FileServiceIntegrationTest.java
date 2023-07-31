package com.innowise.soundfilemicroservice.service;

import com.innowise.soundfilemicroservice.dto.UploadedAudioFileDto;
import com.innowise.soundfilemicroservice.util.Mp3TrackTitleParserUtil;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.innowise.soundfilemicroservice.constant.CamelConstant.DOWNLOAD_FILE_ROUTE;

@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileServiceIntegrationTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.S3)
            .withCommand("awslocal s3api create-bucket --bucket music-file-test-bucket");

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3));
    }

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private FileService fileStoreService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(localStackContainer);
        Assertions.assertTrue(localStackContainer.isCreated());
        Assertions.assertTrue(localStackContainer.isRunning());
        Assertions.assertNotNull(fileStoreService);
        Assertions.assertNotNull(producerTemplate);
    }

    @Test
    @Order(2)
    void mustUploadFile() throws IOException {

        final String fileName = Mp3TrackTitleParserUtil.parseTrackTitle(new FileInputStream("../test-files/Jemi.mp3"));

        MultipartFile file = new MockMultipartFile(fileName, fileName, null, new FileInputStream("../test-files/Jemi.mp3"));

        UploadedAudioFileDto uploadedAudioFileDto = fileStoreService.uploadFile(file);

        Assertions.assertNotNull(uploadedAudioFileDto);

        byte[] actualContentByteArray = producerTemplate.requestBody(DOWNLOAD_FILE_ROUTE,
                uploadedAudioFileDto, InputStream.class).readAllBytes();

        Resource fileResource = new FileSystemResource("../test-files/Jemi.mp3");

        try (InputStream expectedContent = new FileInputStream(fileResource.getFile())) {
            byte[] expectedContentByteArray = expectedContent.readAllBytes();
            Assertions.assertArrayEquals(expectedContentByteArray, actualContentByteArray);
        }
    }
}
