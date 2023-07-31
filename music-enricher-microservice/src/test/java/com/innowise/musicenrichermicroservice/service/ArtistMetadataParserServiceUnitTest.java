package com.innowise.musicenrichermicroservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.musicenrichermicroservice.service.impl.ArtistMetadataParserServiceImpl;
import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArtistMetadataParserServiceUnitTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ArtistMetadataParserServiceImpl artistMetadataParserService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(artistMetadataParserService);
    }

    @Test
    void mustParseArtistMetadata() throws JsonProcessingException {
        final String mockContent = "mockContent";
        final String mockId = "mockId";
        final String mockName = "mockName";
        final int mockPopularity = 54;
        final Set<String> mockGenres = Set.of("mockGenre");
        final String mockSpotifyUri = "mockSpotifyUri";

        SpotifyArtistDto mockSpotifyArtistDto = new SpotifyArtistDto();
        mockSpotifyArtistDto.setId(mockId);
        mockSpotifyArtistDto.setName(mockName);
        mockSpotifyArtistDto.setPopularity(mockPopularity);
        mockSpotifyArtistDto.setGenres(mockGenres);

        final JsonNode artistMetadataMockNode = Mockito.mock(JsonNode.class);
        final JsonNode externalUrlsMockNode = Mockito.mock(JsonNode.class);
        final JsonNode spotifyUrlMockNode = Mockito.mock(JsonNode.class);

        Mockito.when(objectMapper.readTree(mockContent)).thenReturn(artistMetadataMockNode);
        Mockito.when(objectMapper.readValue(mockContent, SpotifyArtistDto.class)).thenReturn(mockSpotifyArtistDto);
        Mockito.when(artistMetadataMockNode.get("external_urls")).thenReturn(externalUrlsMockNode);
        Mockito.when(externalUrlsMockNode.get("spotify")).thenReturn(spotifyUrlMockNode);
        Mockito.when(spotifyUrlMockNode.asText()).thenReturn(mockSpotifyUri);

        SpotifyArtistDto actualSpotifyArtistDto = artistMetadataParserService.parse(mockContent);

        Assertions.assertNotNull(actualSpotifyArtistDto);
        Assertions.assertEquals(mockId, actualSpotifyArtistDto.getId());
        Assertions.assertEquals(mockName, actualSpotifyArtistDto.getName());
        Assertions.assertEquals(mockPopularity, actualSpotifyArtistDto.getPopularity());
        Assertions.assertEquals(mockGenres, actualSpotifyArtistDto.getGenres());
        Assertions.assertEquals(mockSpotifyUri, actualSpotifyArtistDto.getSpotifyUri());
    }
}