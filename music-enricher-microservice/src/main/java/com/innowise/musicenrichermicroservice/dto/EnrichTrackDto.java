package com.innowise.musicenrichermicroservice.dto;

import jakarta.validation.constraints.NotBlank;


public record EnrichTrackDto(

        @NotBlank(message = "Track title is mandatory.")
        String title,

        String creator
) {

}
