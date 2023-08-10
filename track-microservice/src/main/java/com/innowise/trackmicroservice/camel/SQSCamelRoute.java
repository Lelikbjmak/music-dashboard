package com.innowise.trackmicroservice.camel;

import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import com.innowise.trackmicroservice.camel.processor.AlbumIdentificationProcessor;
import com.innowise.trackmicroservice.camel.processor.ArtistIdentificationProcessor;
import com.innowise.trackmicroservice.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static com.innowise.trackmicroservice.constant.CamelConstant.DOWNLOAD_TRACK_METADATA_FROM_SQS_ROUTE;

@Component
@RequiredArgsConstructor
public class SQSCamelRoute extends RouteBuilder {

    private final TrackService trackService;

    private final AlbumIdentificationProcessor albumIdentificationProcessor;

    private final ArtistIdentificationProcessor artistIdentificationProcessor;

    @Override
    public void configure() {

        from(DOWNLOAD_TRACK_METADATA_FROM_SQS_ROUTE)
                .id("GET_ENRICHED_TRACK_SQS")
                .unmarshal().json(SpotifyTrackDto.class)
                .log(LoggingLevel.INFO, "Receive track ${body}.")
                .process(artistIdentificationProcessor)
                .process(albumIdentificationProcessor)
                .bean(trackService, "registerNewTrack(${body})");
    }
}
