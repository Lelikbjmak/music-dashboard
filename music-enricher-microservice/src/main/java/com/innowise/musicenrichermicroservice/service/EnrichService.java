package com.innowise.musicenrichermicroservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innowise.musicenrichermicroservice.dto.EnrichTrackDto;
import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import com.innowise.spotifycommon.dto.SpotifyTrackDto;

public interface EnrichService {

    SpotifyTrackDto enrichTrackMetadataWithSpotify(EnrichTrackDto enrichTrackDto) throws JsonProcessingException;

    SpotifyAlbumDto enrichAlbumMetadataWithSpotify(String id) throws JsonProcessingException;

    SpotifyArtistDto enrichArtistMetadataWithSpotify(String id) throws JsonProcessingException;
}
