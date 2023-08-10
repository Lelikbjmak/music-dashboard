package com.innowise.trackmicroservice.camel;

import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Base64;

import static com.innowise.trackmicroservice.constant.CamelConstant.GET_SPOTIFY_ALBUM_ROUTE;
import static com.innowise.trackmicroservice.constant.CamelConstant.GET_SPOTIFY_ARTIST_ROUTE;

@Component
public class RestCamelRoute extends RouteBuilder {

    @Value(value = "${authentication.basic.credentials}")
    private String basicAuthenticationCredentials;

    @Override
    public void configure() {

        from(GET_SPOTIFY_ALBUM_ROUTE)
                .id("GET_SPOTIFY_ALBUM")
                .setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
                .setHeader(HttpHeaders.AUTHORIZATION, constant("Basic " + Base64.getEncoder().encodeToString(basicAuthenticationCredentials.getBytes())))
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(60)
                        .waitDurationInOpenState(5)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .permittedNumberOfCallsInHalfOpenState(5)
                    .end()
                    .toD("http://localhost:8080/api/v1/enrich/albums/${body}")
                    .unmarshal().json(SpotifyAlbumDto.class)
                .endCircuitBreaker()
                .onFallback().throwException(new RuntimeException());

        from(GET_SPOTIFY_ARTIST_ROUTE)
                .id("GET_SPOTIFY_ARTIST")
                .setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
                .setHeader(HttpHeaders.AUTHORIZATION, constant("Basic " + Base64.getEncoder().encodeToString(basicAuthenticationCredentials.getBytes())))
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(60)
                        .waitDurationInOpenState(5)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .permittedNumberOfCallsInHalfOpenState(5)
                    .end()
                .onFallback().throwException(new RuntimeException())
                    .toD("http://localhost:8080/api/v1/enrich/artists/${body}")
                    .unmarshal().json(SpotifyArtistDto.class)
                .endCircuitBreaker();
    }
}
