package com.innowise.musicenrichermicroservice.spotify;

public record SpotifyObjectRequest(

        String accessToken,

        SpotifyObjectTypeEnum objectTypeEnum,

        String id
) {
}
