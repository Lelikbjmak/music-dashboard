package com.innowise.musicenrichermicroservice.service;

import com.innowise.musicenrichermicroservice.dto.EnrichObjectDto;

public interface SpotifyService {

    String getSpotifyAccessToken();

    String searchObjectByQuery(String searchQuery);

    String searchObjectByTypeAndId(EnrichObjectDto enrichableObjectDto);
}
