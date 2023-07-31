package com.innowise.musicenrichermicroservice.service;

import com.innowise.musicenrichermicroservice.dto.EnrichObjectDto;
import com.innowise.musicenrichermicroservice.dto.EnrichTrackDto;
import com.innowise.musicenrichermicroservice.spotify.SpotifyObjectTypeEnum;
import com.innowise.musicenrichermicroservice.util.SpotifyRequestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import static com.innowise.musicenrichermicroservice.constant.CamelConstant.SPOTIFY_TOKEN_TYPE;

@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpotifyServiceIntegrationTest {

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:6.0.20"))
            .withExposedPorts(6379);

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
            .withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.S3);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint-url", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
        registry.add("spring.redis.host", () -> redisContainer.getHost());
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Value(value = SPOTIFY_TOKEN_TYPE)
    private String tokenKey;

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value(value = "${aws.sqs.queue-name[0]}")
    private String sqsQueueName1;

    @Value(value = "${aws.sqs.queue-name[1]}")
    private String sqsQueueName2;

    @BeforeEach
    void beforeTestCase() throws IOException, InterruptedException {
        String createQueueCommand1 = "awslocal sqs create-queue --queue-name " + sqsQueueName1;
        String createQueueCommand2 = "awslocal sqs create-queue --queue-name " + sqsQueueName2;
        localStackContainer.execInContainer("sh", "-c", createQueueCommand1);
        localStackContainer.execInContainer("sh", "-c", createQueueCommand2);
    }

    @AfterEach
    void afterTestCase() throws IOException, InterruptedException {
        String deleteQueueCommand1 = "awslocal sqs delete-queue --queue-url " + getQueueUrl1();
        String deleteQueueCommand2 = "awslocal sqs delete-queue --queue-url " + getQueueUrl2();
        localStackContainer.execInContainer("sh", "-c", deleteQueueCommand1);
        localStackContainer.execInContainer("sh", "-c", deleteQueueCommand2);
    }


    private String getQueueUrl1() {
        return localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS) +
                "/000000000000/" + sqsQueueName1;
    }

    private String getQueueUrl2() {
        return localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS) +
                "/000000000000/" + sqsQueueName2;
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(spotifyService);
        Assertions.assertTrue(localStackContainer.isRunning());
        Assertions.assertTrue(redisContainer.isRunning());
    }

    @Test
    void mustReturnAccessTokenToSpotify_foundInRedis() {
        String tokenType = "Bearer";
        String expectedToken = UUID.randomUUID().toString();
        int expiration = 3600;

        redisTemplate.opsForValue().setIfAbsent(tokenType, expectedToken, Duration.ofSeconds(expiration));

        String actualToken = spotifyService.getSpotifyAccessToken();

        Assertions.assertNotNull(actualToken);
        Assertions.assertEquals(expectedToken, actualToken);

        redisTemplate.delete(tokenType);
    }

    @Test
    void mustReturnAccessTokenToSpotify_notFoundInRedis() {
        String tokenType = "Bearer";

        String actualToken = spotifyService.getSpotifyAccessToken();

        Assertions.assertNotNull(actualToken);

        String savedToken = redisTemplate.opsForValue().get(tokenKey);

        Assertions.assertNotNull(savedToken);
        Assertions.assertEquals(savedToken, actualToken);

        redisTemplate.delete(tokenType);
    }

    @Test
    void mustSearchObjectByQuery() {
        String query = SpotifyRequestUtil.createTrackSearchQuery(new EnrichTrackDto("Something In The Way", "Nirvana"));
        String json = spotifyService.searchObjectByQuery(query);

        Assertions.assertNotNull(json);
        Assertions.assertTrue(json.contains("Something In The Way"));
        Assertions.assertTrue(json.contains("Nirvana"));
    }

    @Test
    void mustSearchObjectByTypeAndId(@Value(value = "${album.id}") String albumId) {

        String json = spotifyService.searchObjectByTypeAndId(new EnrichObjectDto(SpotifyObjectTypeEnum.ALBUM, albumId));

        Assertions.assertNotNull(json);
        Assertions.assertTrue(json.contains("2UJcKiJxNryhL050F5Z1Fk"));
    }
}