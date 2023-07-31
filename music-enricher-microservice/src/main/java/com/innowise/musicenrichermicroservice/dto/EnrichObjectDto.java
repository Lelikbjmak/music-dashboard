package com.innowise.musicenrichermicroservice.dto;

import com.innowise.musicenrichermicroservice.spotify.SpotifyObjectTypeEnum;
import jakarta.validation.constraints.NotBlank;

public record EnrichObjectDto(

        SpotifyObjectTypeEnum objectTypeEnum,

        @NotBlank(message = "Id is mandatory.")
        String id
) {
}
