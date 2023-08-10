package com.innowise.musicenrichermicroservice.camel;

import com.innowise.camelcommon.dto.UploadedFileDto;
import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static com.innowise.musicenrichermicroservice.constant.CamelConstant.*;

@Component
public class SQSCamelRoute extends RouteBuilder {

    @Override
    public void configure() {

        from(UPLOAD_TO_SQS_ROUTE)
                .id("UPLOAD_TO_SQS_ROUTE")
                .log(LoggingLevel.DEBUG, "Publishing message `${body}` to SQS `{{aws.sqs.queue-name[1]}}`.")
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(60)
                        .waitDurationInOpenState(5)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .permittedNumberOfCallsInHalfOpenState(5)
                    .end()
                    .toD("aws2-sqs:{{aws.sqs.queue-name[1]}}")
                    .log(LoggingLevel.INFO, "Track `${body}` published to SQS `{{aws.sqs.queue-name[1]}}`.")
                    .onFallback().log(LoggingLevel.ERROR, "SQS service to send enriched track data for storing is unavailable.")
                .endCircuitBreaker();

        from(DOWNLOAD_FROM_SQS_ROUTE)
                .id("DOWNLOAD_FROM_SQS_ROUTE")
                .unmarshal().json(UploadedFileDto.class)
                .log(LoggingLevel.INFO, "Received file to download from SQS: ${body}")
                .to(ENRICH_TRACK_ROUTE);
    }
}
