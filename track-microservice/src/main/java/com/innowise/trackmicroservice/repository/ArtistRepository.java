package com.innowise.trackmicroservice.repository;

import com.innowise.trackmicroservice.domain.Artist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ArtistRepository extends MongoRepository<Artist, String> {

    @Query(value = "{'_id': { $in: ?0 } }")
    List<Artist> findByIdList(List<String> idList);

}
