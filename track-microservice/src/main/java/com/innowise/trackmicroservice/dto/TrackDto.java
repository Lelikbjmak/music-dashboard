package com.innowise.trackmicroservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.innowise.trackmicroservice.validation.group.EditGroup;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record TrackDto(

        @NotBlank(message = "Track id is required to delete data about track.",
                groups = {EditGroup.class})
        String id,

        String title,

        Integer discNumber,

        Long durationMs,

        String spotifyUri,

        Integer trackNumber,

        Integer popularity,

        AlbumDto album,

        List<ArtistDto> artistList

) {
}
