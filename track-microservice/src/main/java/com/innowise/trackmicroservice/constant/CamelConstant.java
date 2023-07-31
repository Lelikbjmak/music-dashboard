package com.innowise.trackmicroservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CamelConstant {

    public static final String GET_SPOTIFY_ALBUM_ROUTE = "direct:getAlbum";
    public static final String GET_SPOTIFY_ARTIST_ROUTE = "direct:getArtist";
    public static final String DOWNLOAD_TRACK_METADATA_FROM_SQS_ROUTE = "aws2-sqs:music-data-queue";
}
