package com.innowise.trackmicroservice.service;

import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import com.innowise.trackmicroservice.domain.Artist;
import com.innowise.trackmicroservice.dto.ArtistDto;
import com.innowise.trackmicroservice.exception.ResourceNotFoundException;
import com.innowise.trackmicroservice.mapper.ArtistMapper;
import com.innowise.trackmicroservice.repository.ArtistRepository;
import com.innowise.trackmicroservice.service.impl.ArtistServiceImpl;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;


@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArtistServiceUnitTest {

    @Mock
    private ArtistMapper artistMapper;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ProducerTemplate producerTemplate;

    @InjectMocks
    private ArtistServiceImpl artistService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(artistService);
        Assertions.assertNotNull(artistMapper);
        Assertions.assertNotNull(artistRepository);
        Assertions.assertNotNull(producerTemplate);
    }

    @Test
    void mustSaveArtist() {
        Artist artist = new Artist();
        artist.setId("mockId");

        ArtistDto artistDto = new ArtistDto(
                "mockId",
                null,
                null,
                null,
                null
        );

        Mockito.when(artistRepository.save(artist)).thenReturn(artist);
        Mockito.when(artistMapper.mapToDto(artist)).thenReturn(artistDto);

        ArtistDto actualArtistDto = artistService.save(artist);

        Assertions.assertNotNull(actualArtistDto);
        Assertions.assertEquals(artistDto, actualArtistDto);
    }

    @Test
    void mustDeleteArtistById() {
        final String albumId = "albumId";
        Artist artist = Artist.builder()
                .id(albumId)
                .build();

        Mockito.when(artistRepository.findById(albumId)).thenReturn(Optional.of(artist));

        artistService.delete(albumId);
    }

    @Test
    void mustDeleteArtistById_throwResourceNotFoundException() {
        final String albumId = "albumId";
        Mockito.when(artistRepository.findById(albumId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                artistService.delete(albumId));
    }

    @Test
    void mustEditArtistById() {
        final String artistId = "mockId";

        Artist artist = new Artist();
        artist.setId(artistId);

        ArtistDto artistDto = new ArtistDto(
                artistId,
                null,
                null,
                null,
                null
        );

        Mockito.when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        Mockito.when(artistRepository.save(artist)).thenReturn(artist);
        Mockito.when(artistMapper.mapToDto(artist)).thenReturn(artistDto);

        ArtistDto actualArtistDto = artistService.edit(artistDto);

        Assertions.assertNotNull(actualArtistDto);
        Assertions.assertEquals(artistDto, actualArtistDto);
    }

    @Test
    void mustEditArtistById_throwResourceNotFound() {
        final String artistId = "mockId";

        ArtistDto artistDto = new ArtistDto(
                artistId,
                null,
                null,
                null,
                null
        );

        Mockito.when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                artistService.edit(artistDto));
    }

    @Test
    void mustFindArtistById() {
        final String artistId = "mockId";
        Artist artist = Artist.builder()
                .id(artistId)
                .build();

        ArtistDto artistDto = new ArtistDto(
                artistId,
                null,
                null,
                null,
                null
        );

        Mockito.when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        Mockito.when(artistMapper.mapToDto(artist)).thenReturn(artistDto);

        ArtistDto actualArtistDto = artistService.findById(artistId);

        Assertions.assertNotNull(actualArtistDto);
        Assertions.assertEquals(artistDto, actualArtistDto);
    }

    @Test
    void mustRegisterArtistsIfNotExists() {
        final String artistId1 = "mockId1";
        final String artistId2 = "mockId2";
        List<String> spotifyArtistIdList = List.of(artistId1, artistId2);

        SpotifyArtistDto spotifyArtistDto1 = new SpotifyArtistDto(
                artistId1,
                null,
                0,
                null,
                null
        );

        SpotifyArtistDto spotifyArtistDto2 = new SpotifyArtistDto(
                artistId2,
                null,
                0,
                null,
                null
        );

        Artist artist1 = Artist.builder()
                .id(artistId1)
                .build();

        Artist artist2 = Artist.builder()
                .id(artistId2)
                .build();

        ArtistDto artistDto1 = new ArtistDto(
                artistId1,
                null,
                null,
                null,
                null
        );

        ArtistDto artistDto2 = new ArtistDto(
                artistId2,
                null,
                null,
                null,
                null
        );

        Mockito.when(artistRepository.findById(artistId1)).thenReturn(Optional.empty());
        Mockito.when(artistRepository.findById(artistId2)).thenReturn(Optional.empty());

        Mockito.when(producerTemplate.requestBody("direct:getArtist", artistId1, SpotifyArtistDto.class)).thenReturn(spotifyArtistDto1);
        Mockito.when(producerTemplate.requestBody("direct:getArtist", artistId2, SpotifyArtistDto.class)).thenReturn(spotifyArtistDto2);

        Mockito.when(artistMapper.mapToEntity(spotifyArtistDto1)).thenReturn(artist1);
        Mockito.when(artistMapper.mapToEntity(spotifyArtistDto2)).thenReturn(artist2);

        Mockito.when(artistRepository.save(artist1)).thenReturn(artist1);
        Mockito.when(artistRepository.save(artist2)).thenReturn(artist2);

        Mockito.when(artistMapper.mapToDto(artist1)).thenReturn(artistDto1);
        Mockito.when(artistMapper.mapToDto(artist2)).thenReturn(artistDto2);

        List<ArtistDto> artistDtoList = artistService.registerArtistsIfNotExists(spotifyArtistIdList);
        Assertions.assertNotNull(artistDtoList);
        artistDtoList.forEach(System.out::println);
    }
}