package com.innowise.spotifycommon.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.innowise.spotifycommon.dto.enumeration.AlbumTypeEnum;
import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyAlbumDto {

    private String id;

    private String name;

    private AlbumTypeEnum albumType;

    private String label;

    private int popularity;

    @JsonAlias(value = "total_tracks")
    private int totalTracks;

    @JsonAlias(value = "release_date")
    private Date releaseDate;

    private String spotifyUri;

    private List<String> artistIdList;
}
