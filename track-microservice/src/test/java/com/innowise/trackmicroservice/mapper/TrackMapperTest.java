package com.innowise.trackmicroservice.mapper;

import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import com.innowise.trackmicroservice.domain.Track;
import com.innowise.trackmicroservice.dto.TrackDto;
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

@SpringBootTest
@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrackMapperTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
    }

    @Autowired
    private TrackMapper trackMapper;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(trackMapper);
    }

    @Test
    void mapTrackDtoToEntity() {
        TrackDto trackDto = new TrackDto(
                "mockId",
                "mockTitle",
                1,
                12930L,
                "mockUri",
                60,
                10,
                null,
                null
        );

        Track expectedTrack = new Track(
                "mockId",
                "mockTitle",
                1,
                12930,
                "mockUri",
                60,
                10,
                null,
                null,
                null
        );

        Track actualTrack = trackMapper.mapToEntity(trackDto);

        Assertions.assertNotNull(actualTrack);
        Assertions.assertEquals(expectedTrack, actualTrack);
    }

    @Test
    void mapTrackDtoToEmptyEntity() {
        SpotifyTrackDto spotifyTrackDto = null;
        Track actualTrack = trackMapper.mapToEntity(spotifyTrackDto);

        Assertions.assertNull(actualTrack);
    }

    @Test
    void mapToDto() {
        TrackDto expectedTrackDto = new TrackDto(
                "mockId",
                "mockTitle",
                1,
                12930L,
                "mockUri",
                60,
                10,
                null,
                null
        );

        Track track = new Track(
                "mockId",
                "mockTitle",
                1,
                12930,
                "mockUri",
                60,
                10,
                null,
                null,
                null
        );

        TrackDto actualTrackDto = trackMapper.mapToDto(track);

        Assertions.assertNotNull(actualTrackDto);
        Assertions.assertEquals(expectedTrackDto, actualTrackDto);
    }

    @Test
    void mapToEmptyDto() {
        TrackDto actualTrackDto = trackMapper.mapToDto(null);

        Assertions.assertNull(actualTrackDto);
    }

    @Test
    void mapSpotifyTrackDtoToEntity() {
        SpotifyTrackDto spotifyTrackDto = new SpotifyTrackDto();
        spotifyTrackDto.setId("mockId");
        spotifyTrackDto.setTitle("mockTitle");
        spotifyTrackDto.setDurationMs(1290);
        spotifyTrackDto.setPopularity(10);

        Track expectedTrack = new Track();
        expectedTrack.setId("mockId");
        expectedTrack.setTitle("mockTitle");
        expectedTrack.setDurationMs(1290);
        expectedTrack.setPopularity(10);

        Track actualTrack = trackMapper.mapToEntity(spotifyTrackDto);

        Assertions.assertNotNull(actualTrack);
    }
}