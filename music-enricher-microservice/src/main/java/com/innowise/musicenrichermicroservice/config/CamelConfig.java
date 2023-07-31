package com.innowise.musicenrichermicroservice.config;

import com.innowise.camelcommon.camel.DownloadFileCamelRoute;
import com.innowise.camelcommon.camel.DownloadLocalStorageCamelRoute;
import com.innowise.camelcommon.camel.DownloadS3CamelRoute;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfig {

    @Bean
    public DownloadS3CamelRoute downloadS3CamelRoute() {
        return new DownloadS3CamelRoute();
    }

    @Bean
    public DownloadLocalStorageCamelRoute downloadLocalStorageCamelRoute() {
        return new DownloadLocalStorageCamelRoute();
    }

    @Bean
    public DownloadFileCamelRoute downloadFileCamelRoute() {
        return new DownloadFileCamelRoute();
    }
}
