package com.innowise.trackmicroservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.innowise.spotifycommon.dto.enumeration.AlbumTypeEnum;
import com.innowise.trackmicroservice.validation.group.EditGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class AlbumDto {

    @EqualsAndHashCode.Exclude
    @NotBlank(message = "Album id is required to edit data about album.",
            groups = {EditGroup.class})
    private String id;

    private String name;

    private AlbumTypeEnum albumType;

    private int popularity;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    private Date releaseDate;

    private int totalTracks;

    private String spotifyUri;

    private String label;

    private List<ArtistDto> artistList;

    private List<TrackDto> trackList;
}
