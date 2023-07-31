package com.innowise.musicenrichermicroservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.musicenrichermicroservice.service.impl.AlbumMetadataParserServiceImpl;
import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.spotifycommon.dto.enumeration.AlbumTypeEnum;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Date;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AlbumMetadataParserServiceUnitTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AlbumMetadataParserServiceImpl albumMetadataParserService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(albumMetadataParserService);
    }

    @Test
    void mustParseAlbumMetadata() throws IOException {
        final String mockContent = "mockContent";
        final String mockId = "mockId";
        final String mockName = "mockName";
        final String mockLabel = "mockLabel";
        final int mockPopularity = 54;
        final int mockTotalTrackNumber = 13;
        final AlbumTypeEnum mockAlbumType = AlbumTypeEnum.ALBUM;
        final String mockSpotifyUri = "mockSpotifyUri";

        SpotifyAlbumDto mockSpotifyAlbumDto = new SpotifyAlbumDto();
        mockSpotifyAlbumDto.setId(mockId);
        mockSpotifyAlbumDto.setName(mockName);
        mockSpotifyAlbumDto.setLabel(mockLabel);
        mockSpotifyAlbumDto.setPopularity(mockPopularity);
        mockSpotifyAlbumDto.setTotalTracks(mockTotalTrackNumber);
        mockSpotifyAlbumDto.setReleaseDate(new Date());

        JsonNode metadataMockNode = Mockito.mock(JsonNode.class);
        JsonNode artistsMockNode = Mockito.mock(JsonNode.class);
        JsonNode externalUrlsMockNode = Mockito.mock(JsonNode.class);
        JsonNode spotifyUrlMockNode = Mockito.mock(JsonNode.class);
        JsonNode albumTypeMockNode = Mockito.mock(JsonNode.class);

        Mockito.when(objectMapper.readTree(mockContent)).thenReturn(metadataMockNode);
        Mockito.when(objectMapper.readValue(mockContent, SpotifyAlbumDto.class)).thenReturn(mockSpotifyAlbumDto);
        Mockito.when(metadataMockNode.get("external_urls")).thenReturn(externalUrlsMockNode);
        Mockito.when(externalUrlsMockNode.get("spotify")).thenReturn(spotifyUrlMockNode);
        Mockito.when(spotifyUrlMockNode.asText()).thenReturn(mockSpotifyUri);
        Mockito.when(metadataMockNode.get("album_type")).thenReturn(albumTypeMockNode);
        Mockito.when(albumTypeMockNode.asText()).thenReturn("ALBUM");
        Mockito.when(metadataMockNode.get("artists")).thenReturn(artistsMockNode);

        SpotifyAlbumDto actualAlbumDto = albumMetadataParserService.parse(mockContent);

        Assertions.assertNotNull(actualAlbumDto);
        Assertions.assertEquals(mockId, actualAlbumDto.getId());
        Assertions.assertEquals(mockName, actualAlbumDto.getName());
        Assertions.assertEquals(mockLabel, actualAlbumDto.getLabel());
        Assertions.assertEquals(mockSpotifyUri, actualAlbumDto.getSpotifyUri());
        Assertions.assertEquals(mockAlbumType, actualAlbumDto.getAlbumType());
        Assertions.assertEquals(mockPopularity, actualAlbumDto.getPopularity());
        Assertions.assertEquals(mockTotalTrackNumber, actualAlbumDto.getTotalTracks());
    }
}