package com.innowise.spotifycommon.dto;

import lombok.*;

import java.util.Set;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyArtistDto {

    private String id;

    private String name;

    private int popularity;

    private Set<String> genres;

    private String spotifyIconUri;

    private String spotifyUri;
}
