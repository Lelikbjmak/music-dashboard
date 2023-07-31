package com.innowise.musicenrichermicroservice.service;

import com.innowise.musicenrichermicroservice.spotify.SpotifyAuthenticationResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
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

@Testcontainers
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpotifyAccessTokenServiceIntegrationTest {

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

    @Autowired
    private SpotifyAccessTokenService spotifyAccessTokenService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(spotifyAccessTokenService);
        Assertions.assertTrue(localStackContainer.isRunning());
        Assertions.assertTrue(redisContainer.isRunning());
    }

    @Test
    void mustSaveAndFoundToken() {
        SpotifyAuthenticationResponse spotifyAuthenticationResponse = new SpotifyAuthenticationResponse("token", "test", 3600);
        String savedToken = spotifyAccessTokenService.save(spotifyAuthenticationResponse);

        Assertions.assertNotNull(savedToken);
        Assertions.assertEquals("token", savedToken);

        String foundSavedToken = redisTemplate.opsForValue().get("test");

        Assertions.assertNotNull(foundSavedToken);
    }
}