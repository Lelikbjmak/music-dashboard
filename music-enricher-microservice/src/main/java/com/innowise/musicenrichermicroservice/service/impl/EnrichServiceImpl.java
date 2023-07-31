package com.innowise.musicenrichermicroservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innowise.musicenrichermicroservice.dto.*;
import com.innowise.musicenrichermicroservice.service.EnrichService;
import com.innowise.musicenrichermicroservice.service.MetadataParserService;
import com.innowise.musicenrichermicroservice.service.SpotifyService;
import com.innowise.musicenrichermicroservice.spotify.SpotifyObjectTypeEnum;
import com.innowise.musicenrichermicroservice.util.SpotifyRequestUtil;
import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrichServiceImpl implements EnrichService {

    private final SpotifyService spotifyService;

    private final MetadataParserService<SpotifyTrackDto> trackMetadataParserService;

    private final MetadataParserService<SpotifyAlbumDto> albumMetadataParserService;

    private final MetadataParserService<SpotifyArtistDto> artistMetadataParser;

    @Override
    public SpotifyTrackDto enrichTrackMetadataWithSpotify(EnrichTrackDto enrichTrackDto) throws JsonProcessingException {
        String searchTrackQuery = SpotifyRequestUtil.createTrackSearchQuery(enrichTrackDto);
        String jsonTrackMetadata = spotifyService.searchObjectByQuery(searchTrackQuery);
        return trackMetadataParserService.parse(jsonTrackMetadata);
    }

    @Override
    public SpotifyAlbumDto enrichAlbumMetadataWithSpotify(String id) throws JsonProcessingException {
        EnrichObjectDto enrichObjectDto = new EnrichObjectDto(SpotifyObjectTypeEnum.ALBUM, id);
        String jsonAlbumMetadata = spotifyService.searchObjectByTypeAndId(enrichObjectDto);
        return albumMetadataParserService.parse(jsonAlbumMetadata);
    }

    @Override
    public SpotifyArtistDto enrichArtistMetadataWithSpotify(String id) throws JsonProcessingException {
        EnrichObjectDto enrichObjectDto = new EnrichObjectDto(SpotifyObjectTypeEnum.ARTIST, id);
        String jsonAlbumMetadata = spotifyService.searchObjectByTypeAndId(enrichObjectDto);
        return artistMetadataParser.parse(jsonAlbumMetadata);
    }
}
