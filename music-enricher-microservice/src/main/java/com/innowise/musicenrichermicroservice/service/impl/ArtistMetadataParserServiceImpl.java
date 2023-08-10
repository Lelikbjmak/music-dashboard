package com.innowise.musicenrichermicroservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.musicenrichermicroservice.service.MetadataParserService;
import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtistMetadataParserServiceImpl implements MetadataParserService<SpotifyArtistDto> {

    private final ObjectMapper objectMapper;

    @Override
    public SpotifyArtistDto parse(String json) throws JsonProcessingException {
        JsonNode artistMetadata = objectMapper.readTree(json);
        SpotifyArtistDto spotifyArtistDto = objectMapper.readValue(json, SpotifyArtistDto.class);
        String spotifyIconUri = artistMetadata.get("images").get(0).get("url").asText();
        String spotifyUri = artistMetadata.get("external_urls").get("spotify").asText();
        spotifyArtistDto.setSpotifyUri(spotifyUri);
        spotifyArtistDto.setSpotifyIconUri(spotifyIconUri);
        return spotifyArtistDto;
    }
}
