package com.innowise.trackmicroservice.service;

import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import com.innowise.trackmicroservice.dto.TrackDto;

import java.util.List;

public interface TrackService {

    TrackDto registerNewTrack(SpotifyTrackDto spotifyTrackDto);

    TrackDto save(TrackDto trackDto);

    void delete(String id);

    TrackDto findById(String id);

    TrackDto edit(TrackDto trackDto);

    List<TrackDto> findAll();
}
