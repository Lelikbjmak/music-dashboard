package com.innowise.trackmicroservice.repository;

import com.innowise.trackmicroservice.domain.Album;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlbumRepository extends MongoRepository<Album, String> {
}
