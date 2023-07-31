package com.innowise.trackmicroservice.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tracks")
public class Track {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    private String title;

    private int discNumber;

    private long durationMs;

    private String spotifyUri;

    private int trackNumber;

    private int popularity;

    @DBRef(lazy = true)
    private Album album;

    @DBRef(lazy = true)
    private List<Artist> artistList;

    @CreatedDate
    private Instant createdDate;
}
