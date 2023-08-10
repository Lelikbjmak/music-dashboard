package com.innowise.trackmicroservice.service;

import com.innowise.trackmicroservice.domain.Artist;
import com.innowise.trackmicroservice.dto.ArtistDto;

import java.util.List;

public interface ArtistService {

    ArtistDto save(Artist artist);

    void delete(String id);

    ArtistDto edit(ArtistDto artistDto);

    ArtistDto findById(String id);

    List<ArtistDto> registerArtistsIfNotExists(List<String> spotifyArtistIdList);

    List<ArtistDto> findAll();
}
