package com.innowise.trackmicroservice.controller;

import com.innowise.jwtcommontest.security.TestUserDetails;
import com.innowise.jwtcommontest.util.TestJwtTokenUtil;
import com.innowise.trackmicroservice.dto.AlbumDto;
import com.innowise.trackmicroservice.dto.ArtistDto;
import com.innowise.trackmicroservice.service.ArtistService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.*;

@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArtistControllerIntegrationTest {

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS);

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0.8"))
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setUp(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
        registry.add("spring.data.mongodb.port", () -> mongoDBContainer.getMappedPort(27017));
    }

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ArtistService artistService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(testRestTemplate);
        Assertions.assertNotNull(artistService);
        Assertions.assertTrue(mongoDBContainer.isRunning());
    }

    @Test
    void whenValidInput_thenGetArtistByIdMustReturn200(@Value(value = "${artist.id}") String artistId) {
        HttpEntity<?> getEntity = getHttEntityWithBearerAuth(Set.of("ROLE_USER"), null);
        ResponseEntity<ArtistDto> response = testRestTemplate.exchange("/api/v1/artists/{id}", HttpMethod.GET, getEntity, ArtistDto.class, artistId);

        ArtistDto artistDto = response.getBody();

        Assertions.assertNotNull(artistDto);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void whenValidInput_thenGetArtistByIdMustReturnEmptyArtistDtoAnd200() {
        final String mockId = "SDLSLKSG";

        HttpEntity<?> getEntity = getHttEntityWithBearerAuth(Set.of("ROLE_USER"), null);
        ResponseEntity<ArtistDto> response = testRestTemplate.exchange("/api/v1/artists/{id}", HttpMethod.GET, getEntity, ArtistDto.class, mockId);

        ArtistDto artistDto = response.getBody();

        Assertions.assertNull(artistDto);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void whenValidInput_thenDeleteArtistByIdMustReturn200(@Value(value = "${artist.id}") String artistId) {

        Assertions.assertNotNull(artistService.findById(artistId));

        HttpEntity<?> getEntity = getHttEntityWithBearerAuth(Set.of("ROLE_ADMIN"), null);
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v1/artists/{id}", HttpMethod.DELETE, getEntity, Void.class, artistId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNull(artistService.findById(artistId));
    }

    @Test
    void whenArtistToDeleteNotExist_thenDeleteArtistByIdMustReturn404() {
        final String artistId = "mockAlbumId";

        HttpEntity<?> getEntity = getHttEntityWithBearerAuth(Set.of("ROLE_ADMIN"), null);
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v1/artists/{id}", HttpMethod.DELETE, getEntity, Void.class, artistId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void whenValidInput_thenEditAlbumByIdMustReturn200(@Value(value = "${artist.id}") String albumId) {
        ArtistDto artistBeforeEdit = artistService.findById(albumId);
        final String artistName = "newAlbumName";

        ArtistDto artistDto = new ArtistDto(
                albumId,
                artistName,
                null,
                null,
                null,
                null
        );

        HttpEntity<?> getEntity = getHttEntityWithBearerAuth(Set.of("ROLE_ADMIN"), artistDto);
        ResponseEntity<ArtistDto> response = testRestTemplate.exchange("/api/v1/artists", HttpMethod.PUT, getEntity, ArtistDto.class);

        ArtistDto editedArtistDto = response.getBody();
        ArtistDto artistAfterEdit = artistService.findById(albumId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(editedArtistDto);
        Assertions.assertNotEquals(artistBeforeEdit.name(), editedArtistDto.name());
        Assertions.assertEquals(artistName, artistAfterEdit.name());
    }

    @Test
    void whenArtistToEditNotExist_thenEditArtistByIdMustReturn404() {
        ArtistDto artistDto = new ArtistDto(
                "albumId",
                null,
                null,
                null,
                null,
                null
        );

        HttpEntity<?> getEntity = getHttEntityWithBearerAuth(Set.of("ROLE_ADMIN"), artistDto);
        ResponseEntity<AlbumDto> response = testRestTemplate.exchange("/api/v1/artists", HttpMethod.PUT, getEntity, AlbumDto.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void whenNotValidInput_idFiledIsNotPresent_thenEditAlbumByIdMustReturn403() {
        ArtistDto artistDto = new ArtistDto(
                null,
                null,
                null,
                null,
                null,
                null
        );

        HttpEntity<?> getEntity = getHttEntityWithBearerAuth(Set.of("ROLE_ADMIN"), artistDto);
        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/artists", HttpMethod.PUT, getEntity, String.class);

        String errorMessage = response.getBody();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertTrue(errorMessage.contains("Artist id is required to delete data about artist."));
    }

    @Test
    void whenNotAuthenticated_thenGetAlbumByIdMustReturn401() {
        final String mockId = "SDLSLKSG";
        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/artists/{id}", HttpMethod.GET, HttpEntity.EMPTY, String.class, mockId);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void whenNotAuthenticated_thenDeleteArtistByIdMustReturn401() {
        final String mockId = "SDLSLKSG";
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v1/artists/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, mockId);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void whenNotAuthenticated_thenEditArtistByIdMustReturn401() {
        final String mockId = "SDLSLKSG";
        ArtistDto artistDto = new ArtistDto(
                mockId,
                null,
                null,
                null,
                null,
                null
        );
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v1/artists", HttpMethod.PUT, new HttpEntity<>(artistDto), Void.class, mockId);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void whenNotAuthorized_thenDeleteArtistByIdMustReturn403() {
        final String mockId = "SDLSLKSG";
        HttpEntity<?> getEntity = getHttEntityWithBearerAuth(Set.of("ROLE_USER"), null);
        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/artists/{id}", HttpMethod.DELETE, getEntity, String.class, mockId);

        String errorMessage = response.getBody();

        Assertions.assertNotNull(errorMessage);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertEquals("\"Access Denied\"", errorMessage);
    }

    @Test
    void whenNotAuthorized_thenEditAlbumByIdMustReturn403() {
        final String mockId = "SDLSLKSG";
        ArtistDto artistDto = new ArtistDto(
                mockId,
                null,
                null,
                null,
                null,
                null
        );

        HttpEntity<?> getEntity = getHttEntityWithBearerAuth(Set.of("ROLE_USER"), artistDto);
        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/artists", HttpMethod.PUT, getEntity, String.class);

        String errorMessage = response.getBody();

        Assertions.assertNotNull(errorMessage);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertEquals("\"Access Denied\"", errorMessage);
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

    private HttpEntity<?> getHttEntityWithBearerAuth(Set<String> userRoles, Object body) {
        final String jwt = TestJwtTokenUtil.generateToken(new TestUserDetails(userRoles));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);

        return new HttpEntity<>(body, headers);
    }
}