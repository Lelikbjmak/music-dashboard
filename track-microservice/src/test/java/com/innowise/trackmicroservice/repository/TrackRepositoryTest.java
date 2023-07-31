package com.innowise.trackmicroservice.repository;

import com.innowise.trackmicroservice.domain.Track;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.*;

@DataMongoTest
@Testcontainers
@ActiveProfiles(value = "mongo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrackRepositoryTest {

    @Autowired
    private TrackRepository trackRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0.8"))
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setUp(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.port", () -> mongoDBContainer.getMappedPort(27017));
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(trackRepository);
        Assertions.assertTrue(mongoDBContainer.isRunning());
    }

    @Test
    void mustSaveTrack() {
        Track track = Track.builder()
                .title("title")
                .trackNumber(1)
                .durationMs(56432)
                .popularity(67)
                .build();

        Track savedTrack = trackRepository.save(track);

        Assertions.assertNotNull(savedTrack);
    }

    @Test
    void mustFindTrackById(@Value(value = "${track.id}") String trackId) {
        Optional<Track> optionalTrack = trackRepository.findById(trackId);
        Assertions.assertTrue(optionalTrack.isPresent());
    }

    @Test
    void mustDeleteTrackById(@Value(value = "${track.id}") String trackId) {
        Optional<Track> optionalTrack = trackRepository.findById(trackId);
        Assertions.assertTrue(optionalTrack.isPresent());

        trackRepository.deleteById(trackId);

        Optional<Track> optionalDeletedTrack = trackRepository.findById(trackId);
        Assertions.assertTrue(optionalDeletedTrack.isEmpty());
    }

    @Test
    void mustDeleteTrack(@Value(value = "${track.id}") String trackId) {
        Optional<Track> optionalTrack = trackRepository.findById(trackId);
        Assertions.assertTrue(optionalTrack.isPresent());

        trackRepository.delete(optionalTrack.get());

        Optional<Track> optionalDeletedTrack = trackRepository.findById(trackId);
        Assertions.assertTrue(optionalDeletedTrack.isEmpty());
    }

    @Test
    void mustFindTrackListByAlbumId(@Value(value = "${album.id}") String albumId) {
        List<Track> trackList = trackRepository.findByAlbum(albumId);
        Assertions.assertEquals(1, trackList.size());
    }

    @Test
    void mustFindEmptyTrackListByAlbumId() {
        List<Track> trackList = trackRepository.findByAlbum("fakeAlbumId");
        Assertions.assertEquals(0, trackList.size());
    }

    @BeforeEach
    private void uploadToMongo() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {

            MongoDatabase database = mongoClient.getDatabase("spotify-tracks-test");

            MongoCollection<Document> albumCollection = database.getCollection("albums");
            MongoCollection<Document> artistCollection = database.getCollection("artists");
            MongoCollection<Document> trackCollection = database.getCollection("tracks");

            Document artist = new Document(Map.of(
                    "_id", "1F8usyx5PbYGWxf0bwdXwA",
                    "name", "PHARAOH",
                    "genres", List.of(
                            "russian hip hop",
                            "russian emo rap",
                            "russian grime"
                    ),
                    "popularity", 57,
                    "spotifyUri", "https://open.spotify.com/artist/1F8usyx5PbYGWxf0bwdXwA"
            ));

            Document album = new Document(Map.of(
                    "_id", "5Qcbacw3rlqaXFpbIL5Ys6",
                    "name", "Акид",
                    "albumType", "SINGLE",
                    "popularity", 35,
                    "releaseDate", new Date(),
                    "totalTracks", 1,
                    "spotifyUri", "https://open.spotify.com/album/5Qcbacw3rlqaXFpbIL5Ys6",
                    "label", "Dead Dynasty",
                    "artistList", Collections.singletonList(new Document("$ref", "artists").append("$id", artist.get("_id"))),
                    "_class", "com.innowise.trackmicroservice.domain.Album"
            ));

            Document track = new Document(Map.of(
                    "_id", "79hOg2OcECgvRGGeX0XJuZ",
                    "title", "Акид",
                    "discNumber", 1,
                    "durationMs", 161008,
                    "spotifyUri", "https://open.spotify.com/track/79hOg2OcECgvRGGeX0XJuZ",
                    "trackNumber", 1,
                    "popularity", 45,
                    "artistList", Collections.singletonList(new Document("$ref", "artists").append("$id", artist.get("_id"))),
                    "album", new Document("$ref", "albums").append("$id", album.get("_id"))
            ));

            artistCollection.insertOne(artist);
            albumCollection.insertOne(album);
            trackCollection.insertOne(track);
        }
    }

    @AfterEach
    private void truncateMongo() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {

            MongoDatabase database = mongoClient.getDatabase("spotify-tracks-test");

            MongoCollection<Document> albumCollection = database.getCollection("albums");
            MongoCollection<Document> trackCollection = database.getCollection("tracks");
            MongoCollection<Document> artistCollection = database.getCollection("artists");

            artistCollection.deleteMany(new Document());
            trackCollection.deleteMany(new Document());
            albumCollection.deleteMany(new Document());
        }
    }
}