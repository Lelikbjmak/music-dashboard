package com.innowise.camelcommon.camel;

import com.innowise.camelcommon.exception.AwsServiceUnavailableException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.aws2.s3.AWS2S3Operations;
import org.springframework.stereotype.Component;

import java.io.InputStream;

import static com.innowise.camelcommon.constant.CamelRouteConstant.DOWNLOAD_FROM_S3_ROUTE;

@Component
public class DownloadS3CamelRoute extends RouteBuilder {

    @Override
    public void configure() {

        from(DOWNLOAD_FROM_S3_ROUTE)
                .id("DOWNLOAD_FROM_S3_ROUTE")
                .setHeader(AWS2S3Constants.KEY, simple("${header.fileName}"))
                .setHeader(AWS2S3Constants.S3_OPERATION, constant(AWS2S3Operations.getObject))
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(80)
                        .waitDurationInOpenState(5)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .permittedNumberOfCallsInHalfOpenState(3)
                    .end()
                    .toD("aws2-s3:{{aws.s3.bucket-name}}")
                    .convertBodyTo(InputStream.class)
                    .log(LoggingLevel.DEBUG, "Downloaded file `${header.CamelAwsS3Key}` from S3.")
                    .onFallback()
                        .log(LoggingLevel.ERROR, "S3 service is to download file is unavailable.")
                        .throwException(new AwsServiceUnavailableException("Service is temporary unavailable."))
                .endCircuitBreaker();
    }
}
