package com.innowise.musicenrichermicroservice.service;

import com.innowise.musicenrichermicroservice.exception.NotValidTrackException;
import com.innowise.spotifycommon.dto.SpotifyTrackDto;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrackMetadataParserServiceIntegrationTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.S3);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
    }

    @Autowired
    private MetadataParserService<SpotifyTrackDto> trackMetadataParserService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(trackMetadataParserService);
    }

    @Test
    void mustParseTrackMetadata() {

        final String artistJsonDataSource = "./src/test/resources/json/track-data.json";

        final SpotifyTrackDto expectedTrackDto = new SpotifyTrackDto(
                "6gDEnvN8kU1P0b9X1psvWN",
                "Неон",
                1,
                6,
                49,
                233116,
                "https://open.spotify.com/track/6gDEnvN8kU1P0b9X1psvWN",
                "https://open.spotify.com/track/6gDEnvN8kU1P0b9X1psvWN",
                "3YMe42ahPZSR1vIglZJUIb",
                List.of("1F8usyx5PbYGWxf0bwdXwA", "4h8pGxEIOi7j4me1yhYxlD"),
                List.of("1F8usyx5PbYGWxf0bwdXwA", "4h8pGxEIOi7j4me1yhYxlD")
        );

        try (InputStream stream = new FileInputStream(artistJsonDataSource)) {
            final String jsonString = new String(stream.readAllBytes());
            SpotifyTrackDto actualTrackDto = trackMetadataParserService.parse(jsonString);

            Assertions.assertNotNull(actualTrackDto);
            Assertions.assertEquals(expectedTrackDto, actualTrackDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void mustThrowExceptionWhileParsingNotExistingTrack() {
        final String artistJsonDataSource = "./src/test/resources/json/empty-track-data.json";

        try (InputStream stream = new FileInputStream(artistJsonDataSource)) {
            final String jsonString = new String(stream.readAllBytes());
            Assertions.assertThrows(NotValidTrackException.class, () ->
                    trackMetadataParserService.parse(jsonString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
