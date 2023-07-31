package com.innowise.trackmicroservice.service.impl;

import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.trackmicroservice.domain.Album;
import com.innowise.trackmicroservice.domain.Artist;
import com.innowise.trackmicroservice.domain.Track;
import com.innowise.trackmicroservice.dto.AlbumDto;
import com.innowise.trackmicroservice.dto.TrackDto;
import com.innowise.trackmicroservice.exception.ResourceNotFoundException;
import com.innowise.trackmicroservice.mapper.AlbumMapper;
import com.innowise.trackmicroservice.mapper.TrackListMapper;
import com.innowise.trackmicroservice.repository.AlbumRepository;
import com.innowise.trackmicroservice.repository.ArtistRepository;
import com.innowise.trackmicroservice.repository.TrackRepository;
import com.innowise.trackmicroservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.innowise.trackmicroservice.constant.CamelConstant.GET_SPOTIFY_ALBUM_ROUTE;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumMapper albumMapper;

    private final TrackListMapper trackListMapper;

    private final ProducerTemplate producerTemplate;

    private final AlbumRepository albumRepository;

    private final ArtistRepository artistRepository;

    private final TrackRepository trackRepository;

    @Override
    @Transactional
    public AlbumDto registerNewAlbumIfNotExists(String spotifyAlbumId) {
        Optional<Album> optionalAlbum = albumRepository.findById(spotifyAlbumId);

        if (optionalAlbum.isPresent()) {
            return albumMapper.mapToDto(optionalAlbum.get());
        }

        SpotifyAlbumDto spotifyAlbumDto = producerTemplate.requestBody(GET_SPOTIFY_ALBUM_ROUTE, spotifyAlbumId, SpotifyAlbumDto.class);

        List<Artist> albumArtistList = artistRepository.findByIdList(spotifyAlbumDto.getArtistIdList());

        Album album = albumMapper.mapToEntity(spotifyAlbumDto);
        album.setArtistList(albumArtistList);
        Album savedAlbum = albumRepository.save(album);

        return albumMapper.mapToDto(savedAlbum);
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDto findById(String id) {
        Optional<Album> optionalAlbum = albumRepository.findById(id);

        if (optionalAlbum.isEmpty()) {
            return albumMapper.mapToDto(null);
        }

        Album album = optionalAlbum.get();
        AlbumDto albumDto = albumMapper.mapToDto(album);

        List<Track> trackListForAlbum = trackRepository.findByAlbum(album.getId());
        List<TrackDto> trackDtoListForAlbum = trackListMapper.mapToDtoList(trackListForAlbum);

        albumDto.setTrackList(trackDtoListForAlbum);
        return albumDto;
    }

    @Override
    @Transactional
    @PreAuthorize(value = "hasRole('ADMIN')")
    public void delete(String id) {
        Optional<Album> optionalAlbum = albumRepository.findById(id);

        if (optionalAlbum.isEmpty()) {
            throw new ResourceNotFoundException("Album to delete not found. Id: " + id);
        }

        Album album = optionalAlbum.get();
        albumRepository.delete(album);
    }

    @Override
    @Transactional
    @PreAuthorize(value = "hasRole('ADMIN')")
    public AlbumDto edit(AlbumDto albumToEditDto) {
        final String id = albumToEditDto.getId();
        Optional<Album> optionalAlbum = albumRepository.findById(id);

        if (optionalAlbum.isEmpty()) {
            throw new ResourceNotFoundException("Album to edit not found. Id: " + id);
        }

        Album album = optionalAlbum.get();
        albumMapper.updateEntityFromDto(albumToEditDto, album);

        Album editedAlbum = albumRepository.save(album);

        return albumMapper.mapToDto(editedAlbum);
    }
}
