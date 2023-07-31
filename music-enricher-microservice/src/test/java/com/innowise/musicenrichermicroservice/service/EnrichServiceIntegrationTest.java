package com.innowise.musicenrichermicroservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.innowise.musicenrichermicroservice.dto.EnrichTrackDto;
import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EnrichServiceIntegrationTest {

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
    private EnrichService enrichService;

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
        Assertions.assertNotNull(enrichService);
        Assertions.assertTrue(localStackContainer.isRunning());
        Assertions.assertTrue(redisContainer.isRunning());
    }

    @Test
    void mustReturnEnrichedTrackDto() throws JsonProcessingException {
        final String trackTitle = "Lean Wit Me";
        final String trackCreator = "Juice WRLD";

        final EnrichTrackDto enrichTrackDto = new EnrichTrackDto(
                trackTitle,
                trackCreator
        );

        SpotifyTrackDto spotifyTrackDto = enrichService.enrichTrackMetadataWithSpotify(enrichTrackDto);

        Assertions.assertNotNull(spotifyTrackDto);
        Assertions.assertEquals(trackTitle, spotifyTrackDto.getTitle());
        Assertions.assertFalse(spotifyTrackDto.getTrackArtistIdList().isEmpty());
    }

    @Test
    void mustReturnEnrichedArtistDto() throws JsonProcessingException {
        final String artistId = "7kB4F3PktJEy9jbwI6ujjZ";

        SpotifyArtistDto spotifyArtistDto = enrichService.enrichArtistMetadataWithSpotify(artistId);

        Assertions.assertNotNull(spotifyArtistDto);
        Assertions.assertEquals(artistId, spotifyArtistDto.getId());
    }

    @Test
    void mustReturnEnrichedAlbumDto() throws JsonProcessingException {
        final String albumId = "4TqFHsIfmRgHNB3FLL5pKI";

        SpotifyAlbumDto spotifyAlbumDto = enrichService.enrichAlbumMetadataWithSpotify(albumId);

        Assertions.assertNotNull(spotifyAlbumDto);
        Assertions.assertEquals(albumId, spotifyAlbumDto.getId());
    }
}
