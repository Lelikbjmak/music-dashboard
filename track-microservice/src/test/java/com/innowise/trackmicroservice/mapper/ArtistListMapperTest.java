package com.innowise.trackmicroservice.mapper;

import com.innowise.trackmicroservice.domain.Artist;
import com.innowise.trackmicroservice.dto.ArtistDto;
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

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArtistListMapperTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
    }

    @Autowired
    private ArtistListMapper artistListMapper;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(artistListMapper);
    }

    @Test
    void mustMapToDtoList() {
        List<Artist> artistList = new ArrayList<>();

        artistList.add(Artist.builder()
                .name("artist1")
                .build());

        artistList.add(Artist.builder()
                .name("artist2")
                .build());

        ArtistDto artistDto1 = new ArtistDto(
                null,
                "artist1",
                null,
                0,
                null,
                null
        );

        ArtistDto artistDto2 = new ArtistDto(
                null,
                "artist2",
                null,
                0,
                null,
                null
        );

        List<ArtistDto> expectedArtistDtoList = new ArrayList<>(List.of(artistDto1, artistDto2));

        List<ArtistDto> actualArtistDtoList = artistListMapper.mapToDtoList(artistList);

        Assertions.assertNotNull(actualArtistDtoList);
        Assertions.assertEquals(expectedArtistDtoList, actualArtistDtoList);
    }

    @Test
    void mustMapToEmptyDtoList() {
        List<ArtistDto> actualArtistDtoList = artistListMapper.mapToDtoList(null);

        Assertions.assertNull(actualArtistDtoList);
    }

    @Test
    void mustMapToEntityList() {

        List<ArtistDto> artistDtoList = List.of(
                new ArtistDto(
                        null,
                        "artist1",
                        null,
                        0,
                        null,
                        null
                ),
                new ArtistDto(
                        null,
                        "artist2",
                        null,
                        0,
                        null,
                        null
                )
        );

        List<Artist> expectedArtistList = List.of(
                Artist.builder()
                        .name("artist1")
                        .build(),
                Artist.builder()
                        .name("artist2")
                        .build()
        );

        List<Artist> actualArtistList = artistListMapper.mapToEntityList(artistDtoList);

        Assertions.assertNotNull(actualArtistList);
        Assertions.assertEquals(expectedArtistList, actualArtistList);
    }

    @Test
    void mustMapToEmptyEntityList() {
        List<Artist> actualArtistList = artistListMapper.mapToEntityList(null);

        Assertions.assertNull(actualArtistList);
    }
}
