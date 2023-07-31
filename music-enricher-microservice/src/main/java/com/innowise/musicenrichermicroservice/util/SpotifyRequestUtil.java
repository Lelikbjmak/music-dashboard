package com.innowise.musicenrichermicroservice.util;

import com.innowise.musicenrichermicroservice.dto.EnrichTrackDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SpotifyRequestUtil {

    public static String createTrackSearchQuery(EnrichTrackDto rawTrackDto) {
        StringBuilder searchTrackQuery = new StringBuilder();
        searchTrackQuery
                .append("track:").append(rawTrackDto.title())
                .append(" artist:").append(rawTrackDto.creator())
                .append("&type=track&limit=1");
        return searchTrackQuery.toString();
    }
}
