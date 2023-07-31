package com.innowise.trackmicroservice.repository;

import com.innowise.trackmicroservice.domain.Album;
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
class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

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
        Assertions.assertNotNull(albumRepository);
        Assertions.assertTrue(mongoDBContainer.isRunning());
    }

    @Test
    void mustSaveAlbum() {
        Album album = Album.builder()
                .name("TestAlbum")
                .label("TestLabel")
                .spotifyUri("TestSpotifyUri")
                .build();

        Album savedAlbum = albumRepository.save(album);

        Assertions.assertNotNull(savedAlbum);
    }

    @Test
    void mustFindAlbumById(@Value(value = "${album.id}") String albumId) {
        Optional<Album> optionalAlbum = albumRepository.findById(albumId);
        Assertions.assertTrue(optionalAlbum.isPresent());
    }

    @Test
    void mustDeleteAlbumById(@Value(value = "${album.id}") String albumId) {
        Optional<Album> optionalAlbum = albumRepository.findById(albumId);
        Assertions.assertTrue(optionalAlbum.isPresent());

        albumRepository.deleteById(albumId);

        Optional<Album> optionalDeletedAlbum = albumRepository.findById(albumId);
        Assertions.assertTrue(optionalDeletedAlbum.isEmpty());
    }

    @Test
    void mustDeleteAlbum(@Value(value = "${album.id}") String albumId) {
        Optional<Album> optionalAlbum = albumRepository.findById(albumId);
        Assertions.assertTrue(optionalAlbum.isPresent());

        albumRepository.delete(optionalAlbum.get());

        Optional<Album> optionalDeletedAlbum = albumRepository.findById(albumId);
        Assertions.assertTrue(optionalDeletedAlbum.isEmpty());
    }

    @BeforeEach
    private void uploadToMongo() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {

            MongoDatabase database = mongoClient.getDatabase("spotify-tracks-test");

            MongoCollection<Document> albumCollection = database.getCollection("albums");
            MongoCollection<Document> artistCollection = database.getCollection("artists");

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

            artistCollection.insertOne(artist);
            albumCollection.insertOne(album);
        }
    }

    @AfterEach
    private void truncateMongo() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {

            MongoDatabase database = mongoClient.getDatabase("spotify-tracks-test");

            MongoCollection<Document> albumCollection = database.getCollection("albums");
            MongoCollection<Document> artistCollection = database.getCollection("artists");

            artistCollection.deleteMany(new Document());
            albumCollection.deleteMany(new Document());
        }
    }

}