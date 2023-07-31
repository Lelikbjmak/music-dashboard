package com.innowise.musicenrichermicroservice.spotify;

import lombok.Getter;

@Getter
public enum SpotifyObjectTypeEnum {

    ARTIST("artists"),
    ALBUM("albums");

    private final String urlPart;

    SpotifyObjectTypeEnum(String objectQuery) {
        this.urlPart = objectQuery;
    }
}

