package com.innowise.musicenrichermicroservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.musicenrichermicroservice.exception.NotValidTrackException;
import com.innowise.musicenrichermicroservice.service.MetadataParserService;
import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
//TODO: test if json is null (Not track was found)
public class TrackMetadataParserServiceImpl implements MetadataParserService<SpotifyTrackDto> {

    private final ObjectMapper objectMapper;

    @Override
    public SpotifyTrackDto parse(String json) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(json);
        JsonNode trackMetadata = jsonNode.get("tracks").get("items").get(0);

        if (trackMetadata == null) {
            throw new NotValidTrackException();
        }

        SpotifyTrackDto spotifyTrackDto = objectMapper.readValue(trackMetadata.toString(), SpotifyTrackDto.class);

        JsonNode albumNode = trackMetadata.get("album");

        String albumId = albumNode.get("id").asText();
        JsonNode albumArtistsNode = albumNode.get("artists");
        List<String> albumArtistIdList = new LinkedList<>();
        albumArtistsNode.forEach(artist ->
                albumArtistIdList.add(artist.get("id").asText()));

        JsonNode artistsNode = trackMetadata.get("artists");
        String spotifyUri = trackMetadata.get("external_urls").get("spotify").asText();

        List<String> artistIdList = new LinkedList<>();
        artistsNode.forEach(artist ->
                artistIdList.add(artist.get("id").asText()));

        spotifyTrackDto.setAlbumId(albumId);
        spotifyTrackDto.setSpotifyUri(spotifyUri);
        spotifyTrackDto.setTrackArtistIdList(artistIdList);
        spotifyTrackDto.setAlbumArtistIdList(albumArtistIdList);

        return spotifyTrackDto;
    }
}
