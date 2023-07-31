package com.innowise.camelcommon.camel;

import com.innowise.camelcommon.config.TestConfig;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.innowise.camelcommon.constant.CamelRouteConstant.DOWNLOAD_FROM_LOCAL_STORAGE_ROUTE;

@MockEndpoints
@CamelSpringBootTest
@ActiveProfiles(value = "camel")
@Import(value = TestConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DownloadLocalStorageCamelRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:file:{{local.storage.path}}")
    private MockEndpoint mockLocalStorageUploadEndpoint;

    @Value(value = "${local.storage.path}")
    private String localStoragePath;

    @Value(value = "${local.storage.test-file}")
    private String testFileSourceStorage;

    @AfterEach
    void flushTestLocalStorage() {
        File localStorageDirectory = new File(localStoragePath);

        Assertions.assertTrue(localStorageDirectory.exists());
        Assertions.assertTrue(localStorageDirectory.isDirectory());

        File[] fileArray = localStorageDirectory.listFiles();
        Assertions.assertNotNull(fileArray);

        for (File file : fileArray) {
            if (file.isFile())
                Assertions.assertTrue(file.delete());
        }
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(producerTemplate);
        Assertions.assertNotNull(mockLocalStorageUploadEndpoint);
        Assertions.assertNotNull(localStoragePath);
    }

    @Test
    @Order(2)
    void mustDownloadTextFileFromLocalStorage() throws IOException {

        final String fileNameHeader = "fileName";
        final String fileName = "testFile";
        final String fileBody = "Uploading to LocalStorage...";

        Path filePath = Paths.get(localStoragePath + "/" + fileName);
        Path createdFile = Files.createFile(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(createdFile.toFile()))) {
            writer.write(fileBody);
            writer.flush();
        }

        mockLocalStorageUploadEndpoint.expectedMessageCount(1);
        mockLocalStorageUploadEndpoint.expectedHeaderReceived(fileNameHeader, fileName);

        final String pathHeader = "path";
        final String filePathHeaderValue = localStoragePath + "/" + fileName;

        InputStream receivedBodyFromLocalStorage = producerTemplate.requestBodyAndHeader(DOWNLOAD_FROM_LOCAL_STORAGE_ROUTE, null, pathHeader, filePathHeaderValue, InputStream.class);

        Assertions.assertNotNull(receivedBodyFromLocalStorage);
        Assertions.assertArrayEquals(fileBody.getBytes(), receivedBodyFromLocalStorage.readAllBytes());
    }

    @Test
    @Order(3)
    void mustDownloadAudioFileFromLocalStorage() throws IOException, InterruptedException {

        final String pathHeader = "path";

        Path sourcePath = Path.of(testFileSourceStorage + "/");
        Path destinationPath = Path.of(localStoragePath, sourcePath.getFileName().toString());

        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

        InputStream receivedBodyFromLocalStorage = producerTemplate.requestBodyAndHeader(DOWNLOAD_FROM_LOCAL_STORAGE_ROUTE, null, pathHeader, destinationPath.toString(), InputStream.class);

        Assertions.assertNotNull(receivedBodyFromLocalStorage);
        try (InputStream expectedStreamContent = new FileInputStream(sourcePath.toFile())) {
            byte[] expectedContent = expectedStreamContent.readAllBytes();
            Assertions.assertArrayEquals(expectedContent, receivedBodyFromLocalStorage.readAllBytes());
        }
    }
}