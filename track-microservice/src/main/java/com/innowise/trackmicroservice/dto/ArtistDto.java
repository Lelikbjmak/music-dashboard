package com.innowise.trackmicroservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.innowise.trackmicroservice.validation.group.EditGroup;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record ArtistDto(

        @NotBlank(message = "Artist id is required to delete data about artist.",
                groups = {EditGroup.class})
        String id,

        String name,

        Set<String> genres,

        Integer popularity,

        String spotifyIconUri,

        String spotifyUri

) {
}
