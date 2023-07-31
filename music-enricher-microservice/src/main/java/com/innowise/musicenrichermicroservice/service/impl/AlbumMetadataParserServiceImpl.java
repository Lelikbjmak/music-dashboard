package com.innowise.musicenrichermicroservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.musicenrichermicroservice.service.MetadataParserService;
import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.spotifycommon.dto.enumeration.AlbumTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumMetadataParserServiceImpl implements MetadataParserService<SpotifyAlbumDto> {

    private final ObjectMapper objectMapper;

    @Override
    public SpotifyAlbumDto parse(String json) throws JsonProcessingException {
        JsonNode albumMetadata = objectMapper.readTree(json);

        SpotifyAlbumDto spotifyAlbumDto = objectMapper.readValue(json, SpotifyAlbumDto.class);

        String spotifyUri = albumMetadata.get("external_urls").get("spotify").asText();
        String jsonAlbumType = albumMetadata.get("album_type").asText().toUpperCase();
        JsonNode albumArtistListNode = albumMetadata.get("artists");

        List<String> artistIdList = new LinkedList<>();
        albumArtistListNode.forEach(artist ->
                artistIdList.add(artist.get("id").asText()));

        AlbumTypeEnum albumType = AlbumTypeEnum.valueOf(jsonAlbumType);

        spotifyAlbumDto.setSpotifyUri(spotifyUri);
        spotifyAlbumDto.setAlbumType(albumType);
        spotifyAlbumDto.setArtistIdList(artistIdList);

        return spotifyAlbumDto;
    }
}
