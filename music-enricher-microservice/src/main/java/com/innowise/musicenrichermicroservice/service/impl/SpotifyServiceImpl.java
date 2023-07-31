package com.innowise.musicenrichermicroservice.service.impl;

import com.innowise.musicenrichermicroservice.dto.EnrichObjectDto;
import com.innowise.musicenrichermicroservice.service.SpotifyAccessTokenService;
import com.innowise.musicenrichermicroservice.service.SpotifyService;
import com.innowise.musicenrichermicroservice.spotify.SpotifyAuthenticationResponse;
import com.innowise.musicenrichermicroservice.spotify.SpotifyObjectRequest;
import com.innowise.musicenrichermicroservice.spotify.SpotifySearchRequest;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.innowise.musicenrichermicroservice.constant.CamelConstant.*;

@Service
@RequiredArgsConstructor
public class SpotifyServiceImpl implements SpotifyService {

    @Value(value = SPOTIFY_TOKEN_TYPE)
    private String tokenKey;

    private final ProducerTemplate producerTemplate;

    private final SpotifyAccessTokenService spotifyAccessTokenService;

    @Override
    public String getSpotifyAccessToken() {
        Optional<String> possibleAccessTokenFoeLoggedUser = spotifyAccessTokenService.get(tokenKey);

        if (possibleAccessTokenFoeLoggedUser.isPresent()) {
            return possibleAccessTokenFoeLoggedUser.get();
        }

        SpotifyAuthenticationResponse spotifyAuthenticationResponse = producerTemplate
                .requestBody(SPOTIFY_AUTHENTICATION_ROUTE, null, SpotifyAuthenticationResponse.class);

        return spotifyAccessTokenService.save(spotifyAuthenticationResponse);
    }

    @Override
    public String searchObjectByQuery(String searchQuery) {
        String accessToken = getSpotifyAccessToken();
        SpotifySearchRequest spotifySearchRequest = new SpotifySearchRequest(accessToken, searchQuery);
        return producerTemplate.requestBody(SPOTIFY_SEARCH_ITEM_ROUTE, spotifySearchRequest, String.class);
    }

    @Override
    public String searchObjectByTypeAndId(EnrichObjectDto enrichObjectDto) {
        String accessToken = getSpotifyAccessToken();
        SpotifyObjectRequest spotifyObjectRequest = new SpotifyObjectRequest(accessToken, enrichObjectDto.objectTypeEnum(), enrichObjectDto.id());
        return producerTemplate.requestBody(SPOTIFY_OBJECT_ROUTE, spotifyObjectRequest, String.class);
    }
}
