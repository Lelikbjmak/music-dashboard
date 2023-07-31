package com.innowise.trackmicroservice.service.impl;

import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import com.innowise.trackmicroservice.domain.Artist;
import com.innowise.trackmicroservice.dto.ArtistDto;
import com.innowise.trackmicroservice.exception.ResourceNotFoundException;
import com.innowise.trackmicroservice.mapper.ArtistMapper;
import com.innowise.trackmicroservice.repository.ArtistRepository;
import com.innowise.trackmicroservice.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistMapper artistMapper;

    private final ArtistRepository artistRepository;

    private final ProducerTemplate producerTemplate;

    @Override
    @Transactional
    public ArtistDto save(Artist artist) {
        Artist savedArtist = artistRepository.save(artist);
        return artistMapper.mapToDto(savedArtist);
    }

    @Override
    @Transactional
    @PreAuthorize(value = "hasRole('ADMIN')")
    public void delete(String id) {
        Optional<Artist> optionalArtist = artistRepository.findById(id);

        if (optionalArtist.isEmpty()) {
            throw new ResourceNotFoundException("Artist to delete not found. Id: " + id);
        }

        Artist artist = optionalArtist.get();
        artistRepository.delete(artist);
    }

    @Override
    @Transactional
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ArtistDto edit(ArtistDto artistDto) {
        final String artistToEditId = artistDto.id();
        Optional<Artist> optionalArtist = artistRepository.findById(artistToEditId);

        if (optionalArtist.isEmpty()) {
            throw new ResourceNotFoundException("Artist to delete not found. Id: " + artistToEditId);
        }

        Artist artist = optionalArtist.get();
        artistMapper.updateEntityFromDto(artistDto, artist);

        Artist editedArtist = artistRepository.save(artist);

        return artistMapper.mapToDto(editedArtist);
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistDto findById(String id) {
        Artist artist = artistRepository.findById(id).orElse(null);
        return artistMapper.mapToDto(artist);
    }

    @Override
    @Transactional
    public List<ArtistDto> registerArtistsIfNotExists(List<String> spotifyArtistIdList) {

        List<String> notPresentedArtistIdList = spotifyArtistIdList.stream()
                .filter(artistId -> artistRepository.findById(artistId).isEmpty())
                .toList();

        if (notPresentedArtistIdList.isEmpty()) {
            return new ArrayList<>();
        }

        return notPresentedArtistIdList.stream()
                .map(notPresentedArtistId -> producerTemplate.requestBody("direct:getArtist", notPresentedArtistId, SpotifyArtistDto.class))
                .map(artistMapper::mapToEntity)
                .map(this::save)
                .toList();
    }

}
