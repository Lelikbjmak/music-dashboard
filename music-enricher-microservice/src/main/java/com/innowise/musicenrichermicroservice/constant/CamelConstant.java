package com.innowise.musicenrichermicroservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CamelConstant {

    public static final String SPOTIFY_AUTHENTICATION_ROUTE = "direct:spotifyAuthentication";
    public static final String SPOTIFY_SEARCH_ITEM_ROUTE = "direct:spotifySearchItem";
    public static final String SPOTIFY_OBJECT_ROUTE = "direct:spotifyArtist";
    public static final String UPLOAD_TO_SQS_ROUTE = "direct:toSQS";
    public static final String DOWNLOAD_FROM_SQS_ROUTE = "aws2-sqs:music-file-queue";
    public static final String ENRICH_TRACK_ROUTE = "direct:EnrichTrack";

    public static final String AWS_ENDPOINT_URL_PROPERTY = "${aws.endpoint-url}";
    public static final String SPOTIFY_TOKEN_TYPE = "${spotify.token-type}";

}
