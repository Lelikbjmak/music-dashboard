package com.innowise.soundfilemicroservice.camel;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static com.innowise.soundfilemicroservice.constant.CamelConstant.UPLOAD_TO_SQS_ROUTE;

@Component
public class SQSCamelRoute extends RouteBuilder {

    @Override
    public void configure() {

        from(UPLOAD_TO_SQS_ROUTE)
                .id("UPLOAD_TO_SQS_ROUTE")
                .log(LoggingLevel.DEBUG, "Publishing message `${body}` to SQS `{{aws.sqs.queue-name[0]}}`...")
                .marshal().json()
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .timeoutEnabled(true).timeoutDuration(1000)
                        .failureRateThreshold(60)
                        .waitDurationInOpenState(5)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .permittedNumberOfCallsInHalfOpenState(5)
                    .end()
                    .toD("aws2-sqs:{{aws.sqs.queue-name[0]}}")
                    .log(LoggingLevel.INFO, "Message `${body}` is completely published to SQS `{{aws.sqs.queue-name[0]}}`.")
                .onFallback().log(LoggingLevel.ERROR, "SQS route to enrich track data is unavailable.") // track won't be enriched
                .endCircuitBreaker();
    }
}
