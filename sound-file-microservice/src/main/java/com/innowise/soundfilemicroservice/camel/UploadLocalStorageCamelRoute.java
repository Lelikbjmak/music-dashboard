package com.innowise.soundfilemicroservice.camel;

import com.innowise.soundfilemicroservice.camel.processor.UploadFileCamelProcessor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static com.innowise.soundfilemicroservice.constant.CamelConstant.UPLOAD_TO_LOCAL_STORAGE_ROUTE;

@Component
public class UploadLocalStorageCamelRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        final String storageTypeHeader = "storage";
        final String pathHeader = "path";

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Error occurred: ${exception.message}");

        from(UPLOAD_TO_LOCAL_STORAGE_ROUTE) // use it if S3 is unavailable
                .id("UPLOAD_TO_LOCAL_STORAGE_ROUTE")
                .toD("file:{{local.storage.path}}?fileName=${header.fileName}")
                .setHeader(storageTypeHeader, constant("LOCAL"))
                .setHeader(pathHeader, simple("{{local.storage.path}}/${header.fileName}"))
                .process(new UploadFileCamelProcessor())
                .log(LoggingLevel.DEBUG, "File `${header.fileName}` is completely uploaded to Local Storage.");
    }
}
