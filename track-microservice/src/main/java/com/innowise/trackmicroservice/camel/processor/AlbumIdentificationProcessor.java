package com.innowise.trackmicroservice.camel.processor;

import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import com.innowise.trackmicroservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumIdentificationProcessor implements Processor {

    private final AlbumService albumService;

    @Override
    public void process(Exchange exchange) {
        SpotifyTrackDto body = exchange.getIn().getBody(SpotifyTrackDto.class);
        String albumId = body.getAlbumId();
        albumService.registerNewAlbumIfNotExists(albumId);
    }
}
