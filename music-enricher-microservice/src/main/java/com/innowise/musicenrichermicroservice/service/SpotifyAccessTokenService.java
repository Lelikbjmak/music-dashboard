package com.innowise.musicenrichermicroservice.service;

import com.innowise.musicenrichermicroservice.spotify.SpotifyAuthenticationResponse;

import java.util.Optional;

public interface SpotifyAccessTokenService {

    String save(SpotifyAuthenticationResponse authenticationResponse);

    Optional<String> get(String tokenType);
}
