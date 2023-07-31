package com.innowise.trackmicroservice.mapper;

import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.spotifycommon.dto.enumeration.AlbumTypeEnum;
import com.innowise.trackmicroservice.domain.Album;
import com.innowise.trackmicroservice.dto.AlbumDto;
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
class AlbumMapperTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
    }

    @Autowired
    private AlbumMapper albumMapper;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(albumMapper);
    }

    @Test
    void mapToEntity() {
        SpotifyAlbumDto spotifyAlbumDto = new SpotifyAlbumDto();
        spotifyAlbumDto.setId("mockId");
        spotifyAlbumDto.setName("mockName");

        Album expectedAlbum = new Album();
        expectedAlbum.setId("mockId");
        expectedAlbum.setName("mockName");

        Album actualAlbum = albumMapper.mapToEntity(spotifyAlbumDto);

        Assertions.assertNotNull(actualAlbum);
        Assertions.assertEquals(expectedAlbum, actualAlbum);
    }

    @Test
    void mapToEmptyEntity() {
        Album actualAlbum = albumMapper.mapToEntity(null);

        Assertions.assertNull(actualAlbum);
    }

    @Test
    void mapToDto() {
        Album album = new Album();
        album.setId("mockId");
        album.setName("mockName");
        album.setAlbumType(AlbumTypeEnum.ALBUM);
        album.setLabel("mockLabel");
        album.setSpotifyUri("mockUri");
        album.setTotalTracks(10);
        album.setPopularity(10);

        AlbumDto expectedAlbumDto = new AlbumDto(
                "mockId",
                "mockName",
                AlbumTypeEnum.ALBUM,
                10,
                null,
                10,
                "mockUri",
                "mockLabel",
                null,
                null
        );

        AlbumDto actualAlbumDto = albumMapper.mapToDto(album);

        Assertions.assertNotNull(actualAlbumDto);
        Assertions.assertEquals(expectedAlbumDto.getArtistList(), actualAlbumDto.getArtistList());
        Assertions.assertEquals(expectedAlbumDto.getId(), actualAlbumDto.getId());
        Assertions.assertEquals(expectedAlbumDto.getName(), actualAlbumDto.getName());
        Assertions.assertEquals(expectedAlbumDto.getReleaseDate(), actualAlbumDto.getReleaseDate());
        Assertions.assertEquals(expectedAlbumDto.getAlbumType(), actualAlbumDto.getAlbumType());
    }

    @Test
    void mapToEmptyDto() {
        AlbumDto actualAlbumDto = albumMapper.mapToDto(null);

        Assertions.assertNull(actualAlbumDto);
    }
}