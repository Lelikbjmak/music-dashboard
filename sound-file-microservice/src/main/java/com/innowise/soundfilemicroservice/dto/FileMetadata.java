package com.innowise.soundfilemicroservice.dto;

import java.util.List;

public record FileMetadata(

        String title,

        List<String> creatorList
) {
}
