package com.innowise.musicenrichermicroservice.camel;

import com.innowise.camelcommon.dto.UploadedFileDto;
import com.innowise.camelcommon.exception.AwsServiceUnavailableException;
import com.innowise.musicenrichermicroservice.service.EnrichService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static com.innowise.musicenrichermicroservice.constant.CamelConstant.*;

@Component
@RequiredArgsConstructor
public class SQSCamelRoute extends RouteBuilder {

    private final EnrichService metadataService;

    @Override
    public void configure() {

        from(UPLOAD_TO_SQS_ROUTE)
                .id("UPLOAD_TO_SQS_ROUTE")
                .log(LoggingLevel.DEBUG, "Publishing message `${body}` to SQS `{{aws.sqs.queue-name[1]}}`.")
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .timeoutEnabled(true).timeoutDuration(1000)
                        .failureRateThreshold(60)
                        .waitDurationInOpenState(5)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .permittedNumberOfCallsInHalfOpenState(5)
                    .end()
                    .to("aws2-sqs:{{aws.sqs.queue-name[1]}}")
                    .log(LoggingLevel.INFO, "Message`${body.getName()}` published to SQS `{{aws.sqs.queue-name[1]}}`.")
                .onFallback().log(LoggingLevel.ERROR, "SQS service to send enriched track data for storing is unavailable.")
                .endCircuitBreaker();

        from(DOWNLOAD_FROM_SQS_ROUTE)
                .id("DOWNLOAD_FROM_SQS_ROUTE")
                .unmarshal().json(UploadedFileDto.class)
                .log(LoggingLevel.INFO, "Received file to download from SQS: ${body}")
                .to(ENRICH_TRACK_ROUTE);
    }
}
