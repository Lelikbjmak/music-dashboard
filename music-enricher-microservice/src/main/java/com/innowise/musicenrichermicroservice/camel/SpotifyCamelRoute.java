package com.innowise.musicenrichermicroservice.camel;

import com.innowise.musicenrichermicroservice.exception.SpotifyException;
import com.innowise.musicenrichermicroservice.spotify.SpotifyAuthenticationResponse;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static com.innowise.musicenrichermicroservice.constant.CamelConstant.*;

@Component
public class SpotifyCamelRoute extends RouteBuilder {

    @Override
    public void configure() {

        final String spotifyUnavailableMessage = "Spotify API is temporary unavailable.";

        onException(Exception.class)
                .handled(true)
                .log("Error occurred: ${exception.message}\tResponse body: ${body}");

        from(SPOTIFY_AUTHENTICATION_ROUTE)
                .setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(80)
                        .waitDurationInOpenState(5)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .permittedNumberOfCallsInHalfOpenState(3)
                    .end()
                .setBody(simple("grant_type={{spotify.grant-type}}&client_id={{spotify.client-id}}&client_secret={{spotify.client-secret}}"))
                .to("https://accounts.spotify.com/api/token")
                    .unmarshal().json(SpotifyAuthenticationResponse.class)
                .endCircuitBreaker()
                .onFallback().throwException(new SpotifyException(spotifyUnavailableMessage));

        from(SPOTIFY_SEARCH_ITEM_ROUTE)
                .setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
                .setHeader(HttpHeaders.AUTHORIZATION, simple("Bearer ${body.accessToken()}"))
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(60)
                        .waitDurationInOpenState(5)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .permittedNumberOfCallsInHalfOpenState(5)
                    .end()
                    .toD("https://api.spotify.com/v1/search?q=${body.query()}")
                .onFallback().throwException(new SpotifyException(spotifyUnavailableMessage))
                .endCircuitBreaker()
                .convertBodyTo(String.class);

        from(SPOTIFY_OBJECT_ROUTE)
                .setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
                .setHeader(HttpHeaders.AUTHORIZATION, simple("Bearer ${body.accessToken()}"))
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(60)
                        .waitDurationInOpenState(5)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .permittedNumberOfCallsInHalfOpenState(5)
                    .end()
                    .toD("https://api.spotify.com/v1/${body.objectTypeEnum().getUrlPart()}/${body.id()}")
                .onFallback().throwException(new SpotifyException(spotifyUnavailableMessage))
                .endCircuitBreaker()
                .convertBodyTo(String.class);
    }
}
