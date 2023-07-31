package com.innowise.camelcommon.camel;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;

import static com.innowise.camelcommon.constant.CamelRouteConstant.DOWNLOAD_FROM_LOCAL_STORAGE_ROUTE;

@Component
public class DownloadLocalStorageCamelRoute extends RouteBuilder {

    @Override
    public void configure() {

        final String pathHeader = "path";

        from(DOWNLOAD_FROM_LOCAL_STORAGE_ROUTE)
                .process(exchange -> {
                    String fileDestination = exchange.getIn().getHeader(pathHeader, String.class);
                    File file = new File(fileDestination);
                    exchange.getIn().setBody(new FileInputStream(file));
                })
                .log(LoggingLevel.DEBUG, "Downloaded file `${header.fileName}` from Locale Storage.");
    }
}
