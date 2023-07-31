package com.innowise.musicenrichermicroservice.service;

import com.innowise.spotifycommon.dto.SpotifyArtistDto;
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
import java.util.Set;

@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArtistMetadataParserServiceIntegrationTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.S3);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
    }

    @Autowired
    private MetadataParserService<SpotifyArtistDto> artistMetadataParserService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(artistMetadataParserService);
    }

    @Test
    void mustParseArtistSpotifyMetadata() {
        final String artistJsonDataSource = "./src/test/resources/json/artist-data.json";

        final SpotifyArtistDto expectedArtistDto = new SpotifyArtistDto(
                "1F8usyx5PbYGWxf0bwdXwA",
                "PHARAOH",
                57,
                Set.of("russian emo rap", "russian grime", "russian hip hop"),
                "https://open.spotify.com/artist/1F8usyx5PbYGWxf0bwdXwA"
        );

        try (InputStream stream = new FileInputStream(artistJsonDataSource)) {
            final String jsonString = new String(stream.readAllBytes());
            SpotifyArtistDto actualArtistDto = artistMetadataParserService.parse(jsonString);

            Assertions.assertNotNull(actualArtistDto);
            Assertions.assertEquals(expectedArtistDto, actualArtistDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
