package com.innowise.trackmicroservice.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "artists")
public class Artist {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    private String name;

    private Set<String> genres;

    private int popularity;

    private String spotifyUri;

    @CreatedDate
    private Instant createdDate;
}