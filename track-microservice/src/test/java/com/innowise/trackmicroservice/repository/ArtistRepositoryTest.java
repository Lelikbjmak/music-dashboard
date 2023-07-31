package com.innowise.trackmicroservice.repository;

import com.innowise.trackmicroservice.domain.Artist;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@DataMongoTest
@Testcontainers
@ActiveProfiles(value = "mongo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArtistRepositoryTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0.8"))
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setUp(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.port", () -> mongoDBContainer.getMappedPort(27017));
    }

    @BeforeEach
    public void uploadToMongo() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            MongoDatabase database = mongoClient.getDatabase("spotify-tracks-test");

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

            artistCollection.insertOne(artist);
        }
    }

    @AfterEach
    public void truncateMongo() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            MongoDatabase database = mongoClient.getDatabase("spotify-tracks-test");
            MongoCollection<Document> artistCollection = database.getCollection("artists");
            artistCollection.deleteMany(new Document());
        }
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(artistRepository);
        Assertions.assertTrue(mongoDBContainer.isRunning());
    }

    @Test
    void mustSaveArtist() {
        Artist artist = Artist.builder()
                .name("TestAlbum")
                .popularity(56)
                .spotifyUri("TestSpotifyUri")
                .build();

        Artist sabvedArtist = artistRepository.save(artist);

        Assertions.assertNotNull(sabvedArtist);
    }

    @Test
    void mustFindArtistById(@Value(value = "${artist.id}") String artistId) {
        Optional<Artist> optionalArtist = artistRepository.findById(artistId);
        artistRepository.findAll().forEach(System.out::println);
        Assertions.assertTrue(optionalArtist.isPresent());
    }

    @Test
    void mustDeleteArtistById(@Value(value = "${artist.id}") String artistId) {
        Optional<Artist> optionalArtist = artistRepository.findById(artistId);
        Assertions.assertTrue(optionalArtist.isPresent());

        artistRepository.deleteById(artistId);

        Optional<Artist> optionalDeletedAlbum = artistRepository.findById(artistId);
        Assertions.assertTrue(optionalDeletedAlbum.isEmpty());
    }

    @Test
    void mustDeleteArtist(@Value(value = "${artist.id}") String artistId) {
        Optional<Artist> optionalArtist = artistRepository.findById(artistId);
        Assertions.assertTrue(optionalArtist.isPresent());

        artistRepository.delete(optionalArtist.get());

        Optional<Artist> optionalDeletedAlbum = artistRepository.findById(artistId);
        Assertions.assertTrue(optionalDeletedAlbum.isEmpty());
    }

    @Test
    void mustFindArtistListByIdList(@Value(value = "${artist.id}") String artistId) {
        List<String> idList = List.of(artistId, "fakeId");
        List<Artist> artistList = artistRepository.findByIdList(idList);
        Assertions.assertEquals(1, artistList.size());
    }

    @Test
    void mustFindEmptyArtistListByIdList() {
        List<String> idList = List.of("artistId", "fakeId");
        List<Artist> artistList = artistRepository.findByIdList(idList);
        Assertions.assertEquals(0, artistList.size());
    }
}