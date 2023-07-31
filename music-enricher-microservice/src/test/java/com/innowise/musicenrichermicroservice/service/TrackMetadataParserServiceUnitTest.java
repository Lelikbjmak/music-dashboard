package com.innowise.musicenrichermicroservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.musicenrichermicroservice.service.impl.TrackMetadataParserServiceImpl;
import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrackMetadataParserServiceUnitTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TrackMetadataParserServiceImpl trackMetadataParserService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(trackMetadataParserService);
    }

    @Test
    void mustParseTrackMetadata() throws JsonProcessingException {
        final String mockContent = "mockContent";

        final String mockId = "mockId";
        final String mockName = "mockName";
        final int mockPopularity = 49;
        final int mockTrackNumber = 6;
        final int mockDiscNumber = 1;
        final int mockDuration = 233116;
        final String mockSpotifyUri = "mockSpotifyUri";
        final String mockAlbumId = "mockAlbumId";

        SpotifyTrackDto mockSpotifyTrackDto = new SpotifyTrackDto();
        mockSpotifyTrackDto.setId(mockId);
        mockSpotifyTrackDto.setTitle(mockName);
        mockSpotifyTrackDto.setPopularity(mockPopularity);
        mockSpotifyTrackDto.setDiscNumber(mockDiscNumber);
        mockSpotifyTrackDto.setTrackNumber(mockTrackNumber);
        mockSpotifyTrackDto.setDurationMs(mockDuration);
        mockSpotifyTrackDto.setSpotifyUri(mockSpotifyUri);

        JsonNode trackMetadataMockNode = Mockito.mock(JsonNode.class);
        JsonNode trackArrayMockNode = Mockito.mock(JsonNode.class);
        JsonNode trackArrayItemMockNode = Mockito.mock(JsonNode.class);
        JsonNode firstItemMockNode = Mockito.mock(JsonNode.class);
        JsonNode trackAlbumMockNode = Mockito.mock(JsonNode.class);
        JsonNode trackAlbumIdMockNode = Mockito.mock(JsonNode.class);
        JsonNode externalUrlsMockNode = Mockito.mock(JsonNode.class);
        JsonNode spotifyUrlMockNode = Mockito.mock(JsonNode.class);

        Mockito.when(objectMapper.readTree(mockContent)).thenReturn(trackMetadataMockNode);
        Mockito.when(trackMetadataMockNode.get("tracks")).thenReturn(trackArrayMockNode);
        Mockito.when(trackArrayMockNode.get("items")).thenReturn(trackArrayItemMockNode);
        Mockito.when(trackArrayItemMockNode.get(0)).thenReturn(firstItemMockNode);

        Mockito.when(objectMapper.readValue(firstItemMockNode.toString(), SpotifyTrackDto.class)).thenReturn(mockSpotifyTrackDto);

        Mockito.when(firstItemMockNode.get("album")).thenReturn(trackAlbumMockNode);
        Mockito.when(trackAlbumMockNode.get("id")).thenReturn(trackAlbumIdMockNode);
        Mockito.when(trackAlbumIdMockNode.asText()).thenReturn(mockAlbumId);
        Mockito.when(trackAlbumMockNode.get("artists")).thenReturn(trackAlbumMockNode);

        Mockito.when(firstItemMockNode.get("artists")).thenReturn(firstItemMockNode);
        Mockito.when(firstItemMockNode.get("external_urls")).thenReturn(externalUrlsMockNode);
        Mockito.when(externalUrlsMockNode.get("spotify")).thenReturn(spotifyUrlMockNode);
        Mockito.when(spotifyUrlMockNode.asText()).thenReturn(mockSpotifyUri);

        SpotifyTrackDto actualSpotifyTrackDto = trackMetadataParserService.parse(mockContent);

        Assertions.assertNotNull(actualSpotifyTrackDto);
        Assertions.assertEquals(mockId, actualSpotifyTrackDto.getId());
        Assertions.assertEquals(mockName, actualSpotifyTrackDto.getTitle());
        Assertions.assertEquals(mockTrackNumber, actualSpotifyTrackDto.getTrackNumber());
        Assertions.assertEquals(mockSpotifyUri, actualSpotifyTrackDto.getSpotifyUri());
        Assertions.assertEquals(mockDiscNumber, actualSpotifyTrackDto.getDiscNumber());
        Assertions.assertEquals(mockPopularity, actualSpotifyTrackDto.getPopularity());
        Assertions.assertEquals(mockAlbumId, actualSpotifyTrackDto.getAlbumId());
        Assertions.assertEquals(mockDuration, actualSpotifyTrackDto.getDurationMs());
    }
}