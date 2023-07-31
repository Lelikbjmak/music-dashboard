package com.innowise.camelcommon.camel;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.io.InputStream;

import static com.innowise.camelcommon.constant.CamelRouteConstant.*;

@Component
public class DownloadFileCamelRoute extends RouteBuilder {

    @Override
    public void configure() {

        final String fileNameHeader = "fileName";
        final String pathHeader = "path";

        from(DOWNLOAD_FILE_ROUTE)
                .log(LoggingLevel.DEBUG, "Downloading file `${body.fileName()}`...")
                .setHeader(fileNameHeader, simple("${body.fileName()}"))
                .choice()
                .when().simple("${body.storage()} == 'S3'")
                .to(DOWNLOAD_FROM_S3_ROUTE)
                .otherwise()
                .setHeader(pathHeader, simple("${body.path()}"))
                .to(DOWNLOAD_FROM_LOCAL_STORAGE_ROUTE)
                .end()
                .convertBodyTo(InputStream.class);
    }
}
