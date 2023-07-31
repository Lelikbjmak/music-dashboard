package com.innowise.soundfilemicroservice.camel;

import com.innowise.soundfilemicroservice.camel.processor.SetFileTitleCamelProcessor;
import com.innowise.soundfilemicroservice.camel.processor.UploadFileCamelProcessor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static com.innowise.soundfilemicroservice.constant.CamelConstant.UPLOAD_S3_ROUTE;
import static com.innowise.soundfilemicroservice.constant.CamelConstant.UPLOAD_TO_LOCAL_STORAGE_ROUTE;

@Component
public class UploadS3CamelRoute extends RouteBuilder {

    @Override
    public void configure() {

        final String storageTypeHeader = "storage";
        final String pathHeader = "path";

        onException(S3Exception.class, SdkClientException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "S3Route: Error occurred: ${exception.message}")
                .to(UPLOAD_TO_LOCAL_STORAGE_ROUTE);

        from(UPLOAD_S3_ROUTE)
                .id("UPLOAD_S3_ROUTE")
                .process(new SetFileTitleCamelProcessor())
                .setBody(simple("${body.getInputStream()}"))
                .setHeader(AWS2S3Constants.KEY, simple("${header.fileName}"))
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(80)
                        .waitDurationInOpenState(5)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .permittedNumberOfCallsInHalfOpenState(3)
                    .end()
                    .toD("aws2-s3:{{aws.s3.bucket-name}}")
                    .setHeader(storageTypeHeader, constant("S3"))
                    .setHeader(pathHeader, simple("{{aws.s3.bucket-name}}/${header.fileName}"))
                    .process(new UploadFileCamelProcessor())
                    .log(LoggingLevel.DEBUG, "File `${header.fileName}` is completely uploaded to S3.")
                    .onFallback().to(UPLOAD_TO_LOCAL_STORAGE_ROUTE)
                .endCircuitBreaker();
    }
}
