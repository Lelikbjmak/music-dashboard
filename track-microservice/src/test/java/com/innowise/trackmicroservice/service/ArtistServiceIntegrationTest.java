package com.innowise.trackmicroservice.service;

import com.innowise.trackmicroservice.domain.Artist;
import com.innowise.trackmicroservice.dto.ArtistDto;
import com.innowise.trackmicroservice.exception.ResourceNotFoundException;
import com.innowise.trackmicroservice.repository.ArtistRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArtistServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0.8"))
            .withExposedPorts(27017);

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
        registry.add("spring.data.mongodb.port", () -> mongoDBContainer.getMappedPort(27017));
    }

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtistRepository artistRepository;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(artistService);
        Assertions.assertNotNull(artistRepository);
        Assertions.assertTrue(mongoDBContainer.isRunning());
        Assertions.assertTrue(localStackContainer.isRunning());
    }

    @Test
    void mustSaveArtist() {
        final String artistId = "mockId";
        final String artistName = "mockName";

        Artist artist = Artist.builder()
                .id(artistId)
                .name(artistName)
                .build();

        ArtistDto savedArtistDto = artistService.save(artist);

        Assertions.assertNotNull(savedArtistDto);

        Assertions.assertEquals(artistId, savedArtistDto.id());
        Assertions.assertEquals(artistName, savedArtistDto.name());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void mustDeleteArtistById(@Value(value = "${artist.id}") String artistId) {
        Assertions.assertTrue(artistRepository.findById(artistId).isPresent());

        artistService.delete(artistId);

        Assertions.assertTrue(artistRepository.findById(artistId).isEmpty());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void mustDeleteArtistById_throwNotFoundException() {
        final String artistId = "mockId";
        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                artistService.delete(artistId));
    }

    @Test
    @WithMockUser
    void mustDeleteArtistById_notAuthorized(@Value(value = "${artist.id}") String artistId) {
        Assertions.assertThrows(AccessDeniedException.class, () ->
                artistService.delete(artistId));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void mustEditArtistById(@Value(value = "${artist.id}") String artistId) {
        final String artistName = "newArtistName";

        Assertions.assertNotEquals(artistName, artistRepository.findById(artistId).get().getName());

        ArtistDto artistDto = new ArtistDto(
                artistId,
                artistName,
                null,
                null,
                null
        );

        ArtistDto editedArtistDto = artistService.edit(artistDto);

        Assertions.assertNotNull(editedArtistDto);
        Assertions.assertEquals(artistName, editedArtistDto.name());
        Assertions.assertEquals(artistName, artistRepository.findById(artistId).get().getName());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void mustEditArtistById_throwNotFoundException() {
        final String artistId = "mockId";

        ArtistDto artistDto = new ArtistDto(
                artistId,
                null,
                null,
                null,
                null
        );

        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                artistService.edit(artistDto));
    }

    @Test
    @WithMockUser
    void mustEditArtistById_notAuthorized() {
        Assertions.assertThrows(AccessDeniedException.class, () ->
                artistService.edit(null));
    }

    @Test
    void mustFindUserById(@Value(value = "${artist.id}") String artistId) {
        ArtistDto artistDto = artistService.findById(artistId);
        Assertions.assertNotNull(artistDto);
        Assertions.assertEquals(artistId, artistDto.id());
    }

    @Test
    void mustFindEmptyUserById() {
        ArtistDto artistDto = artistService.findById("mockId");
        Assertions.assertNull(artistDto);
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