package com.innowise.musicenrichermicroservice.spotify;

public record SpotifySearchRequest(

        String accessToken,

        String query
) {
}
