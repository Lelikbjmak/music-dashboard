package com.innowise.musicenrichermicroservice.service;


import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.spotifycommon.dto.enumeration.AlbumTypeEnum;
import org.joda.time.LocalDateTime;
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
import java.util.ArrayList;
import java.util.List;

@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AlbumMetadataParserServiceIntegrationTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.S3);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
    }

    @Autowired
    private MetadataParserService<SpotifyAlbumDto> spotifyAlbumMetadataParserService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(spotifyAlbumMetadataParserService);
    }

    @Test
    void mustParseAlbumSpotifyMetadata() {
        final String artistJsonDataSource = "./src/test/resources/json/album-data.json";

        final SpotifyAlbumDto expectedAlbumDto = new SpotifyAlbumDto(
                "4MlBJlydRNG0rAoSgh1Ia3",
                "Дух Мира",
                AlbumTypeEnum.ALBUM,
                "Worldwide Baby",
                51,
                13,
                new LocalDateTime(2023, 5, 26, 3, 0, 0).toDate(),
                "https://open.spotify.com/album/4MlBJlydRNG0rAoSgh1Ia3",
                "https://open.spotify.com/album/4MlBJlydRNG0rAoSgh1Ia3",
                new ArrayList<>(List.of("7kB4F3PktJEy9jbwI6ujjZ"))
        );

        try (InputStream stream = new FileInputStream(artistJsonDataSource)) {
            final String jsonString = new String(stream.readAllBytes());
            SpotifyAlbumDto actualAlbumDto = spotifyAlbumMetadataParserService.parse(jsonString);

            Assertions.assertNotNull(actualAlbumDto);
            Assertions.assertEquals(expectedAlbumDto, actualAlbumDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
