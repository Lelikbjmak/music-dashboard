package com.innowise.trackmicroservice.mapper;

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

import java.util.List;

@SpringBootTest
@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrackListMapperTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
    }

    @Autowired
    private TrackListMapper trackListMapper;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(trackListMapper);
    }

    @Test
    void mustMapToDtoList() {
        List<Track> trackList = List.of(
                Track.builder()
                        .title("title1")
                        .build(),
                Track.builder()
                        .title("title2")
                        .build()
        );

        List<TrackDto> expectedTrackDtoList = List.of(
                new TrackDto(
                        null,
                        "title1",
                        0,
                        0L,
                        null,
                        0,
                        0,
                        null,
                        null
                ),
                new TrackDto(
                        null,
                        "title2",
                        0,
                        0L,
                        null,
                        0,
                        0,
                        null,
                        null
                )
        );

        List<TrackDto> actualTrackDtoList = trackListMapper.mapToDtoList(trackList);

        Assertions.assertNotNull(actualTrackDtoList);
        Assertions.assertEquals(expectedTrackDtoList, actualTrackDtoList);
    }

    @Test
    void mustMapToEmptyDtoList() {
        List<TrackDto> actualTrackDtoList = trackListMapper.mapToDtoList(null);

        Assertions.assertNull(actualTrackDtoList);
    }

    @Test
    void mustMapToEntityList() {
        List<TrackDto> trackDtoList = List.of(
                new TrackDto(
                        null,
                        "title1",
                        0,
                        0L,
                        null,
                        0,
                        0,
                        null,
                        null
                ),
                new TrackDto(
                        null,
                        "title2",
                        0,
                        0L,
                        null,
                        0,
                        0,
                        null,
                        null
                )
        );

        List<Track> expectedTrackList = List.of(
                Track.builder()
                        .title("title1")
                        .build(),
                Track.builder()
                        .title("title2")
                        .build()
        );

        List<Track> actualTrackList = trackListMapper.mapToEntityList(trackDtoList);

        Assertions.assertNotNull(actualTrackList);
        Assertions.assertEquals(expectedTrackList, actualTrackList);
    }

    @Test
    void mustMapToEmptyEntityList() {
        List<Track> actualTrackList = trackListMapper.mapToEntityList(null);

        Assertions.assertNull(actualTrackList);
    }
}
