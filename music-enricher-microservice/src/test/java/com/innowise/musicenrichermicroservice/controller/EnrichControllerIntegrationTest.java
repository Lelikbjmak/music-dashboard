package com.innowise.musicenrichermicroservice.controller;

import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static com.innowise.musicenrichermicroservice.constant.YamlPropertyConstant.IN_MEMORY_USER_PASSWORD_PROPERTY;
import static com.innowise.musicenrichermicroservice.constant.YamlPropertyConstant.IN_MEMORY_USER_USERNAME_PROPERTY;

@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EnrichControllerIntegrationTest {

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:6.0.20"))
            .withExposedPorts(6379);

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.S3);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
        registry.add("spring.redis.host", () -> redisContainer.getHost());
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value(value = IN_MEMORY_USER_USERNAME_PROPERTY)
    private String inMemoryUserUsername;

    @Value(value = IN_MEMORY_USER_PASSWORD_PROPERTY)
    private String inMemoryUserPassword;

    @Value(value = "${aws.sqs.queue-name[1]}")
    private String sqsQueueName;

    @BeforeEach
    void beforeTestCase() throws IOException, InterruptedException {
        String createQueueCommand = "awslocal sqs create-queue --queue-name " + sqsQueueName;
        localStackContainer.execInContainer("sh", "-c", createQueueCommand);
    }

    @AfterEach
    void afterTestCase() throws IOException, InterruptedException {
        String deleteQueueCommand = "awslocal sqs delete-queue --queue-url " + getQueueUrl();
        localStackContainer.execInContainer("sh", "-c", deleteQueueCommand);
    }

    private String getQueueUrl() {
        return localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS) +
                "/000000000000/" + sqsQueueName;
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(testRestTemplate);
        Assertions.assertTrue(localStackContainer.isRunning());
        Assertions.assertTrue(redisContainer.isRunning());
    }

    @Test
    void whenValidInput_thenEnrichAlbumByIdMustReturn200() {
        final String spotifyAlbumId = "3YMe42ahPZSR1vIglZJUIb";
        final HttpEntity<Void> httpRequestEntity = getAuthenticationRequestEntity();

        ResponseEntity<SpotifyAlbumDto> response = testRestTemplate.exchange("/api/v1/enrich/albums/{id}", HttpMethod.GET, httpRequestEntity, SpotifyAlbumDto.class, spotifyAlbumId);

        Assertions.assertNotNull(response);
        SpotifyAlbumDto actualSpotifyAlbumDto = response.getBody();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(actualSpotifyAlbumDto);
        Assertions.assertEquals(spotifyAlbumId, actualSpotifyAlbumDto.getId());
    }

    @Test
    void whenNotAuthenticated_thenEnrichAlbumByIdMustReturn401() {
        final String spotifyAlbumId = "3YMe42ahPZSR1vIglZJUIb";

        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/enrich/albums/{id}", HttpMethod.GET, HttpEntity.EMPTY, String.class, spotifyAlbumId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        String message = response.getBody();

        Assertions.assertNotNull(message);
        Assertions.assertTrue(message.contains("Full authentication"));
    }

    @Test
    void whenValidInput_thenEnrichArtistByIdMustReturn200() {
        final String spotifyArtistId = "16ZNqMkDZrzd8fTXeN2kUH";
        final HttpEntity<Void> httpRequestEntity = getAuthenticationRequestEntity();

        ResponseEntity<SpotifyArtistDto> response = testRestTemplate.exchange("/api/v1/enrich/artists/{id}", HttpMethod.GET, httpRequestEntity, SpotifyArtistDto.class, spotifyArtistId);

        Assertions.assertNotNull(response);
        SpotifyArtistDto actualSpotifyArtistDto = response.getBody();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(actualSpotifyArtistDto);
        Assertions.assertEquals(spotifyArtistId, actualSpotifyArtistDto.getId());
    }

    @Test
    void whenNotAuthenticated_thenEnrichArtistByIdMustReturn401() {
        final String spotifyArtistId = "16ZNqMkDZrzd8fTXeN2kUH";

        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/enrich/artists/{id}", HttpMethod.GET, HttpEntity.EMPTY, String.class, spotifyArtistId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        String message = response.getBody();

        Assertions.assertNotNull(message);
        Assertions.assertTrue(message.contains("Full authentication"));
    }

    private HttpEntity<Void> getAuthenticationRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(inMemoryUserUsername, inMemoryUserPassword);
        return new HttpEntity<>(headers);
    }

}