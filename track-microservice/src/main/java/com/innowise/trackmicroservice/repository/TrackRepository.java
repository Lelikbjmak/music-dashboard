package com.innowise.trackmicroservice.repository;

import com.innowise.trackmicroservice.domain.Track;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TrackRepository extends MongoRepository<Track, String> {

    @Query(value = "{'album.$id': ?0 }")
    List<Track> findByAlbum(String albumId);

}
