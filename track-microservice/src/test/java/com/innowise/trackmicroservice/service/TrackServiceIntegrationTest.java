package com.innowise.trackmicroservice.service;

import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import com.innowise.trackmicroservice.dto.TrackDto;
import com.innowise.trackmicroservice.exception.ResourceNotFoundException;
import com.innowise.trackmicroservice.repository.TrackRepository;
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
class TrackServiceIntegrationTest {

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
    private TrackService trackService;

    @Autowired
    private TrackRepository trackRepository;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(trackService);
        Assertions.assertNotNull(trackRepository);
        Assertions.assertTrue(mongoDBContainer.isRunning());
        Assertions.assertTrue(localStackContainer.isRunning());
    }

    @Test
    void mustRegisterNewTrack(@Value(value = "${artist.id}") String artistId, @Value(value = "${album.id}") String albumId) {
        SpotifyTrackDto spotifyTrackDto = new SpotifyTrackDto(
                "mockId",
                "title",
                1,
                13,
                67,
                2953L,
                "spotifyUri",
                "spotifyUri",
                albumId,
                List.of(artistId),
                List.of(artistId)
        );

        TrackDto trackDto = trackService.registerNewTrack(spotifyTrackDto);

        Assertions.assertNotNull(trackDto);
        Assertions.assertNotNull(trackDto.album());
        Assertions.assertNotNull(trackDto.artistList());
        Assertions.assertTrue(trackRepository.findById("mockId").isPresent());
    }

    @Test
    void mustSaveTrack() {
        TrackDto trackDto = new TrackDto(
                null,
                "title",
                1,
                2365L,
                "spotifyUri",
                "spotifyUri",
                12,
                67,
                null,
                null
        );

        TrackDto savedTrackDto = trackService.save(trackDto);

        Assertions.assertNotNull(savedTrackDto);
        Assertions.assertNotNull(savedTrackDto.id());
        Assertions.assertTrue(trackRepository.findById(savedTrackDto.id()).isPresent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void mustDeleteTrackById(@Value(value = "${track.id}") String trackId) {
        Assertions.assertTrue(trackRepository.findById(trackId).isPresent());

        trackService.delete(trackId);

        Assertions.assertTrue(trackRepository.findById(trackId).isEmpty());
    }

    @Test
    @WithMockUser
    void mustDeleteTrackById_notAutrhorized(@Value(value = "${track.id}") String trackId) {
        Assertions.assertThrows(AccessDeniedException.class, () ->
                trackService.delete(trackId));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void mustDeleteTrackById_trackNotFound() {
        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                trackService.delete("fakeId"));
    }

    @Test
    void mustFindTrackById(@Value(value = "${track.id}") String trackId) {
        TrackDto trackDto = trackService.findById(trackId);

        Assertions.assertNotNull(trackDto);
        Assertions.assertTrue(trackRepository.findById(trackId).isPresent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void mustEditTrackById(@Value(value = "${track.id}") String trackId) {
        final String newTrackTitle = "newTitle";

        Assertions.assertNotEquals(newTrackTitle, trackRepository.findById(trackId).get().getTitle());

        TrackDto trackDtoToEdit = new TrackDto(
                trackId,
                newTrackTitle,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        TrackDto editedTrackDto = trackService.edit(trackDtoToEdit);

        Assertions.assertNotNull(editedTrackDto);
        Assertions.assertEquals(newTrackTitle, editedTrackDto.title());
        Assertions.assertEquals(newTrackTitle, trackRepository.findById(trackId).get().getTitle());
    }

    @Test
    @WithMockUser
    void mustEditTrackById_notAuthorized(@Value(value = "${track.id}") String trackId) {
        final String newTrackTitle = "newTitle";

        TrackDto trackDtoToEdit = new TrackDto(
                trackId,
                newTrackTitle,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Assertions.assertThrows(AccessDeniedException.class, () ->
                trackService.edit(trackDtoToEdit));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void mustEditTrackById_trackNotFound() {
        final String trackId = "fakeId";
        final String newTrackTitle = "newTitle";

        TrackDto trackDtoToEdit = new TrackDto(
                trackId,
                newTrackTitle,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                trackService.edit(trackDtoToEdit));
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