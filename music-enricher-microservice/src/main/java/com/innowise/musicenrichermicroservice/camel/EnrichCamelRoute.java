package com.innowise.musicenrichermicroservice.camel;

import com.innowise.musicenrichermicroservice.service.EnrichService;
import com.innowise.musicenrichermicroservice.util.Mp3TrackMetadataParserUtil;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static com.innowise.camelcommon.constant.CamelRouteConstant.DOWNLOAD_FILE_ROUTE;
import static com.innowise.musicenrichermicroservice.constant.CamelConstant.ENRICH_TRACK_ROUTE;
import static com.innowise.musicenrichermicroservice.constant.CamelConstant.UPLOAD_TO_SQS_ROUTE;

@Component
@RequiredArgsConstructor
public class EnrichCamelRoute extends RouteBuilder {

    private final EnrichService enrichService;

    @Override
    public void configure() {

        from(ENRICH_TRACK_ROUTE)
                .to(DOWNLOAD_FILE_ROUTE)
                .bean(Mp3TrackMetadataParserUtil.class, "extractRowTrackMetadata(${body})")
                .bean(enrichService, "enrichTrackMetadataWithSpotify(${body})")
                .marshal().json()
                .to(UPLOAD_TO_SQS_ROUTE);
    }
}
