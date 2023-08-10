package com.innowise.spotifycommon.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyTrackDto {

    private String id;

    @JsonAlias(value = "name")
    private String title;

    @JsonAlias(value = "disc_number")
    private int discNumber;

    @JsonAlias(value = "track_number")
    private int trackNumber;

    private int popularity;

    @JsonAlias(value = "duration_ms")
    private long durationMs;

    private String spotifyUri;

    private String spotifyIconUri;

    private String albumId;

    private List<String> trackArtistIdList;

    private List<String> albumArtistIdList;
}
