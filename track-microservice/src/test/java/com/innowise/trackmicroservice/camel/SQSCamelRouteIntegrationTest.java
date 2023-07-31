package com.innowise.trackmicroservice.camel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import com.innowise.trackmicroservice.repository.AlbumRepository;
import com.innowise.trackmicroservice.repository.ArtistRepository;
import com.innowise.trackmicroservice.repository.TrackRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.io.IOException;
import java.util.*;

@Testcontainers
@CamelSpringBootTest
@ActiveProfiles(value = "camel")
@MockEndpoints(value = "aws2-sqs:music-data-queue")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SQSCamelRouteIntegrationTest {

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
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:aws2-sqs:{{aws.sqs.queue-name}}")
    private MockEndpoint mockSqsEndpoint;

    @Value(value = "${aws.sqs.queue-name}")
    private String sqsQueueName;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SqsClient sqsClient;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @BeforeEach
    void beforeTestCase() throws IOException, InterruptedException {
        mockSqsEndpoint.reset();
        String createQueueCommand = "awslocal sqs create-queue --queue-name " + sqsQueueName;
        localStackContainer.execInContainer("sh", "-c", createQueueCommand);
    }

    @AfterEach
    void afterTestCase() throws IOException, InterruptedException {
        String deleteQueueCommand = "awslocal sqs delete-queue --queue-url " + getQueueUrl();
        localStackContainer.execInContainer("sh", "-c", deleteQueueCommand);
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(sqsClient);
        Assertions.assertNotNull(sqsQueueName);
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(mockSqsEndpoint);
        Assertions.assertNotNull(trackRepository);
        Assertions.assertNotNull(producerTemplate);
    }

    @Test
    void mustGainTrackDataFromSQS(
            @Value(value = "${album.id}") String albumId,
            @Value(value = "${artist.id}") String artistId) throws JsonProcessingException {

        final String trackId = UUID.randomUUID().toString();

        SpotifyTrackDto spotifyTrackDto = new SpotifyTrackDto(
                trackId,
                "title",
                1,
                12,
                56,
                3436L,
                "spotifyUri",
                albumId,
                List.of(artistId),
                List.of(artistId)
        );

        sqsClient.sendMessage(SendMessageRequest.builder()
                .messageBody(objectMapper.writeValueAsString(spotifyTrackDto))
                .queueUrl(getQueueUrl())
                .build());

        mockSqsEndpoint.expectedMessageCount(1);
        mockSqsEndpoint.expectedBodiesReceived(objectMapper.writeValueAsString(spotifyTrackDto));


    }

    private String getQueueUrl() {
        return localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS) +
                "/000000000000/" + sqsQueueName;
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