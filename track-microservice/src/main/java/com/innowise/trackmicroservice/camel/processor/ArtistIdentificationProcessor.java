package com.innowise.trackmicroservice.camel.processor;

import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import com.innowise.trackmicroservice.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ArtistIdentificationProcessor implements Processor {

    private final ArtistService artistService;

    @Override
    public void process(Exchange exchange) {
        SpotifyTrackDto body = exchange.getIn().getBody(SpotifyTrackDto.class);
        List<String> trackArtistIdList = body.getTrackArtistIdList();
        List<String> albumArtistIdList = body.getAlbumArtistIdList();

        List<String> allArtists = Stream.concat(trackArtistIdList.stream(), albumArtistIdList.stream())
                .distinct()
                .toList();

        artistService.registerArtistsIfNotExists(allArtists);
    }
}
