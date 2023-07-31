package com.innowise.trackmicroservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.innowise.spotifycommon.dto.enumeration.AlbumTypeEnum;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "albums")
public class Album {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    private String name;

    private AlbumTypeEnum albumType;

    private int popularity;

    @Field(targetType = FieldType.DATE_TIME)
    @DateTimeFormat(pattern = "YYYY-MM-DD")
    private Date releaseDate;

    private int totalTracks;

    private String spotifyUri;

    private String label;

    @DBRef(lazy = true)
    private List<Artist> artistList;

    @CreatedDate
    private Instant createdDate;
}
