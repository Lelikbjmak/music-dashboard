package com.innowise.trackmicroservice.mapper;

import com.innowise.spotifycommon.dto.SpotifyArtistDto;
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

import java.util.Set;

@SpringBootTest
@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArtistMapperTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
    }

    @Autowired
    private ArtistMapper artistMapper;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(artistMapper);
    }

    @Test
    void mapToEntityFromSpotifyArtistDto() {
        SpotifyArtistDto spotifyArtistDto = new SpotifyArtistDto();
        spotifyArtistDto.setId("mockId");
        spotifyArtistDto.setName("mockName");
        spotifyArtistDto.setPopularity(10);

        Artist expectedArtist = new Artist();
        expectedArtist.setName("mockName");
        expectedArtist.setPopularity(10);
        expectedArtist.setId("mockId");

        Artist actualArtist = artistMapper.mapToEntity(spotifyArtistDto);

        Assertions.assertNotNull(actualArtist);
        Assertions.assertEquals(expectedArtist, actualArtist);
    }

    @Test
    void mapToEmptyEntityFromArtistDto() {
        ArtistDto artistDto = null;
        Artist actualArtist = artistMapper.mapToEntity(artistDto);

        Assertions.assertNull(actualArtist);
    }

    @Test
    void mapToEntityFromArtistDto() {
        ArtistDto artistDto = new ArtistDto(
                "id",
                "name",
                null,
                1,
                "uri",
                "uri"
        );

        Artist expectedArtist = new Artist();
        expectedArtist.setName("name");
        expectedArtist.setPopularity(1);
        expectedArtist.setId("id");
        expectedArtist.setGenres(null);
        expectedArtist.setSpotifyUri("uri");
        expectedArtist.setSpotifyIconUri("uri");

        Artist actualArtist = artistMapper.mapToEntity(artistDto);

        Assertions.assertNotNull(actualArtist);
        Assertions.assertEquals(expectedArtist, actualArtist);
    }

    @Test
    void mapToEmptyEntityFromSpotifyArtist() {
        SpotifyArtistDto artistDto = null;
        Artist actualArtist = artistMapper.mapToEntity(artistDto);

        Assertions.assertNull(actualArtist);
    }

    @Test
    void mapToDto() {
        Artist artist = new Artist(
                "mockId",
                "mockName",
                Set.of("ROCK"),
                10,
                "mockUri",
                "mockUri",
                null
        );

        ArtistDto expectedArtistDto = new ArtistDto(
                "mockId",
                "mockName",
                Set.of("ROCK"),
                10,
                "mockUri",
                "mockUri"
        );

        ArtistDto actualArtistDto = artistMapper.mapToDto(artist);

        Assertions.assertNotNull(actualArtistDto);
        Assertions.assertEquals(expectedArtistDto, actualArtistDto);
    }

    @Test
    void mapToEmptyDto() {
        ArtistDto actualArtistDto = artistMapper.mapToDto(null);

        Assertions.assertNull(actualArtistDto);
    }
}