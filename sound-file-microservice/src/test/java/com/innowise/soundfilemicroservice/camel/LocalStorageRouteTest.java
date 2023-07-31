package com.innowise.soundfilemicroservice.camel;

import com.innowise.soundfilemicroservice.util.Mp3TrackTitleParserUtil;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.innowise.soundfilemicroservice.constant.CamelConstant.UPLOAD_S3_ROUTE;
import static com.innowise.soundfilemicroservice.constant.CamelConstant.UPLOAD_TO_LOCAL_STORAGE_ROUTE;


@MockEndpoints
@CamelSpringBootTest
@ActiveProfiles(value = "camel")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LocalStorageRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:file:{{local.storage.path}}")
    private MockEndpoint mockLocalStorageUploadEndpoint;

    @Value(value = "${local.storage.path}")
    private String localStoragePath;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(producerTemplate);
        Assertions.assertNotNull(mockLocalStorageUploadEndpoint);
        Assertions.assertNotNull(localStoragePath);
    }

    @Test
    @Order(2)
    void mustUploadToLocalStorage() throws InterruptedException, IOException {

        final String fileNameHeader = "fileName";

        File uploadedFile = new File("../test-files/Jemi.mp3");

        Assertions.assertTrue(uploadedFile.exists());

        final String fileName = Mp3TrackTitleParserUtil.parseTrackTitle(new FileInputStream(uploadedFile));

        mockLocalStorageUploadEndpoint.expectedMessageCount(1);
        mockLocalStorageUploadEndpoint.expectedFileExists(localStoragePath + "/" + fileName);

        producerTemplate.sendBodyAndHeader(UPLOAD_TO_LOCAL_STORAGE_ROUTE, new FileInputStream(uploadedFile), fileNameHeader, fileName);

        mockLocalStorageUploadEndpoint.assertIsSatisfied();

        File testFile = new File(localStoragePath + "/" + fileName);
        Assertions.assertTrue(testFile.delete());
    }

    @Test
    @Order(3)
    void mustUploadToLocalStorageDueToS3Error() throws IOException {

        File uploadedFile = new File("../test-files/Jemi.mp3");

        Assertions.assertTrue(uploadedFile.exists());

        final String fileName = Mp3TrackTitleParserUtil.parseTrackTitle(new FileInputStream(uploadedFile));

        MultipartFile file = new MockMultipartFile(uploadedFile.getName(), uploadedFile.getName(), null, new FileInputStream(uploadedFile));

        mockLocalStorageUploadEndpoint.expectedMessageCount(1);
        mockLocalStorageUploadEndpoint.expectedFileExists(localStoragePath + "/" + fileName);

        producerTemplate.sendBody(UPLOAD_S3_ROUTE, file);

        File testFile = new File(localStoragePath + "/" + fileName);
        Assertions.assertTrue(testFile.delete());
    }

}