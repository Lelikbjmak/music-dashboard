package com.innowise.trackmicroservice.service.impl;

import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import com.innowise.trackmicroservice.domain.Album;
import com.innowise.trackmicroservice.domain.Artist;
import com.innowise.trackmicroservice.domain.Track;
import com.innowise.trackmicroservice.dto.TrackDto;
import com.innowise.trackmicroservice.exception.ResourceNotFoundException;
import com.innowise.trackmicroservice.mapper.TrackMapper;
import com.innowise.trackmicroservice.repository.AlbumRepository;
import com.innowise.trackmicroservice.repository.ArtistRepository;
import com.innowise.trackmicroservice.repository.TrackRepository;
import com.innowise.trackmicroservice.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;

    private final TrackMapper trackMapper;

    private final AlbumRepository albumRepository;

    private final ArtistRepository artistRepository;

    @Override
    @Transactional
    public TrackDto registerNewTrack(SpotifyTrackDto spotifyTrackDto) {

        List<Artist> trackArtistList = artistRepository.findByIdList(spotifyTrackDto.getTrackArtistIdList());
        Album album = albumRepository.findById(spotifyTrackDto.getAlbumId()).orElse(null);

        Track track = trackMapper.mapToEntity(spotifyTrackDto);

        track.setAlbum(album);
        track.setArtistList(trackArtistList);

        Track savedTrack = trackRepository.save(track);

        return trackMapper.mapToDto(savedTrack);
    }

    @Override
    @Transactional
    public TrackDto save(TrackDto trackDto) {
        Track track = trackMapper.mapToEntity(trackDto);
        Track savedTrack = trackRepository.save(track);
        return trackMapper.mapToDto(savedTrack);
    }

    @Override
    @Transactional
    @PreAuthorize(value = "hasRole('ADMIN')")
    public void delete(String id) {
        Optional<Track> optionalTrack = trackRepository.findById(id);

        if (optionalTrack.isEmpty()) {
            throw new ResourceNotFoundException("Track to delete not found. Id: " + id);
        }

        Track trackToDelete = optionalTrack.get();
        trackRepository.delete(trackToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackDto findById(String id) {
        Track track = trackRepository.findById(id).orElse(null);
        return trackMapper.mapToDto(track);
    }

    @Override
    @Transactional
    @PreAuthorize(value = "hasRole('ADMIN')")
    public TrackDto edit(TrackDto trackToEditDto) {
        final String trackToEditId = trackToEditDto.id();
        Optional<Track> optionalTrack = trackRepository.findById(trackToEditId);

        if (optionalTrack.isEmpty()) {
            throw new ResourceNotFoundException("Track to delete not found. Id: " + trackToEditId);
        }

        Track trackToEdit = optionalTrack.get();
        trackMapper.updateEntityFromDto(trackToEditDto, trackToEdit);
        Track editedTrack = trackRepository.save(trackToEdit);

        return trackMapper.mapToDto(editedTrack);
    }

}
