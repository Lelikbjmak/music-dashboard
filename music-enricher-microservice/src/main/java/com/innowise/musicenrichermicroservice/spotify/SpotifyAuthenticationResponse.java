package com.innowise.musicenrichermicroservice.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyAuthenticationResponse(

        @JsonProperty(value = "access_token")
        String accessToken,

        @JsonProperty(value = "token_type")
        String tokenType,

        @JsonProperty(value = "expires_in")
        long expiration
) {
}
