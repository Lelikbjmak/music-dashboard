package com.innowise.musicenrichermicroservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innowise.musicenrichermicroservice.service.EnrichService;
import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/enrich")
public class EnrichController {

    private final EnrichService enrichService;

    /**
     * @param id - Track id in Spotify
     */
    @GetMapping(value = "/albums/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public SpotifyAlbumDto enrichAlbumById(@PathVariable(name = "id") String id) throws JsonProcessingException {
        return enrichService.enrichAlbumMetadataWithSpotify(id);
    }

    /**
     * @param id - Artist id in Spotify
     */
    @GetMapping(value = "/artists/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public SpotifyArtistDto enrichArtistById(@PathVariable(name = "id") String id) throws JsonProcessingException {
        return enrichService.enrichArtistMetadataWithSpotify(id);
    }
}
