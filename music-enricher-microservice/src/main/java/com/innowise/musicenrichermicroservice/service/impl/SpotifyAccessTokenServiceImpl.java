package com.innowise.musicenrichermicroservice.service.impl;

import com.innowise.musicenrichermicroservice.service.SpotifyAccessTokenService;
import com.innowise.musicenrichermicroservice.spotify.SpotifyAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpotifyAccessTokenServiceImpl implements SpotifyAccessTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String save(SpotifyAuthenticationResponse authenticationResponse) {
        final String accessToken = authenticationResponse.accessToken();
        final long expiration = authenticationResponse.expiration();
        final String tokenType = authenticationResponse.tokenType();

        redisTemplate.opsForValue().setIfAbsent(tokenType, accessToken, Duration.ofSeconds(expiration));
        return accessToken;
    }

    @Override
    public Optional<String> get(String tokenType) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(tokenType));
    }
}
