package com.innowise.trackmicroservice.service;

import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.trackmicroservice.domain.Album;
import com.innowise.trackmicroservice.domain.Artist;
import com.innowise.trackmicroservice.dto.AlbumDto;
import com.innowise.trackmicroservice.exception.ResourceNotFoundException;
import com.innowise.trackmicroservice.mapper.AlbumMapper;
import com.innowise.trackmicroservice.mapper.TrackListMapper;
import com.innowise.trackmicroservice.repository.AlbumRepository;
import com.innowise.trackmicroservice.repository.ArtistRepository;
import com.innowise.trackmicroservice.repository.TrackRepository;
import com.innowise.trackmicroservice.service.impl.AlbumServiceImpl;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.innowise.trackmicroservice.constant.CamelConstant.GET_SPOTIFY_ALBUM_ROUTE;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AlbumServiceUnitTest {

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private TrackListMapper trackListMapper;

    @Mock
    private ProducerTemplate producerTemplate;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private TrackRepository trackRepository;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(albumMapper);
        Assertions.assertNotNull(trackListMapper);
        Assertions.assertNotNull(producerTemplate);
        Assertions.assertNotNull(albumRepository);
        Assertions.assertNotNull(albumRepository);
        Assertions.assertNotNull(trackRepository);
        Assertions.assertNotNull(albumService);
    }

    @Test
    void mustRegisterNewAlbumIfNotExists_AlbumNotExists() {
        final String mockAlbumId = "mockId";
        final String mockArtistId = "mockArtistId";

        SpotifyAlbumDto mockSpotifyAlbumDto = new SpotifyAlbumDto();
        mockSpotifyAlbumDto.setId(mockAlbumId);
        mockSpotifyAlbumDto.setArtistIdList(List.of(mockArtistId));

        Artist mockArtist = new Artist();
        mockArtist.setId(mockArtistId);

        Album mockAlbum = Album.builder()
                .id(mockAlbumId)
                .build();

        AlbumDto mockAlbumDto = new AlbumDto();
        mockAlbumDto.setId(mockAlbumId);

        Mockito.when(albumRepository.findById(mockAlbumId)).thenReturn(Optional.empty());
        Mockito.when(producerTemplate.requestBody(GET_SPOTIFY_ALBUM_ROUTE, mockAlbumId, SpotifyAlbumDto.class)).thenReturn(mockSpotifyAlbumDto);

        Mockito.when(artistRepository.findByIdList(mockSpotifyAlbumDto.getArtistIdList())).thenReturn(List.of(mockArtist));
        Mockito.when(albumMapper.mapToEntity(mockSpotifyAlbumDto)).thenReturn(mockAlbum);
        Mockito.when(albumRepository.save(mockAlbum)).thenReturn(mockAlbum);
        Mockito.when(albumMapper.mapToDto(mockAlbum)).thenReturn(mockAlbumDto);

        AlbumDto actualAlbumDto = albumService.registerNewAlbumIfNotExists(mockAlbumId);

        Assertions.assertNotNull(actualAlbumDto);
        Assertions.assertEquals(mockAlbumDto, actualAlbumDto);
    }

    @Test
    void mustRegisterNewAlbumIfNotExists_AlbumAlreadyExists() {
        final String mockAlbumId = "mockId";

        Album mockAlbum = Album.builder()
                .id(mockAlbumId)
                .build();

        AlbumDto mockAlbumDto = new AlbumDto();
        mockAlbumDto.setId(mockAlbumId);

        Mockito.when(albumRepository.findById(mockAlbumId)).thenReturn(Optional.of(mockAlbum));
        Mockito.when(albumMapper.mapToDto(mockAlbum)).thenReturn(mockAlbumDto);

        AlbumDto actualAlbumDto = albumService.registerNewAlbumIfNotExists(mockAlbumId);

        Assertions.assertNotNull(actualAlbumDto);
        Assertions.assertEquals(mockAlbumDto, actualAlbumDto);
    }

    @Test
    void mustReturnAlbumById_albumNotFound() {
        final String albumId = "mockAlbumId";
        Mockito.when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        Mockito.when(albumMapper.mapToDto(null)).thenReturn(null);

        AlbumDto actualAlbumDto = albumService.findById(albumId);

        Assertions.assertNull(actualAlbumDto);
    }

    @Test
    void mustReturnAlbumById_albumFound() {
        final String albumId = "mockAlbumId";
        Album album = Album.builder()
                .id(albumId)
                .name("mockName")
                .build();

        AlbumDto mockAlbumDto = new AlbumDto();
        mockAlbumDto.setId(albumId);
        mockAlbumDto.setName("mockName");

        Mockito.when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        Mockito.when(albumMapper.mapToDto(album)).thenReturn(mockAlbumDto);

        AlbumDto actualAlbumDto = albumService.findById(albumId);

        Assertions.assertNotNull(actualAlbumDto);
        Assertions.assertEquals(mockAlbumDto, actualAlbumDto);

    }

    @Test
    void mustDeleteAlbumById() {
        final String albumId = "mockId";
        Album album = Album.builder()
                .id(albumId)
                .build();

        Mockito.when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.delete(albumId);
    }

    @Test
    void mustDeleteAlbumById_throwExceptionResourceNotFound() {
        final String albumId = "mockId";

        Mockito.when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                albumService.delete(albumId));
    }

    @Test
    void mustEditAlbumById() {
        final String albumId = "mockId";
        final String newAlbumName = "newName";

        Album album = Album.builder()
                .id(albumId)
                .name("oldName")
                .build();

        Album savedAlbum = Album.builder()
                .id(albumId)
                .name(newAlbumName)
                .build();

        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(albumId);
        albumDto.setName(newAlbumName);

        AlbumDto expectedAlbumDto = new AlbumDto();
        albumDto.setId(albumId);
        albumDto.setName(newAlbumName);

        Mockito.when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        Mockito.when(albumRepository.save(album)).thenReturn(savedAlbum);
        Mockito.when(albumMapper.mapToDto(savedAlbum)).thenReturn(expectedAlbumDto);

        AlbumDto actualEditedDto = albumService.edit(albumDto);

        Assertions.assertNotNull(actualEditedDto);
        Assertions.assertEquals(expectedAlbumDto, actualEditedDto);
    }

    @Test
    void mustEditAlbumById_throwResourceNotFoundException() {
        final String albumId = "mockAlbumId";
        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(albumId);

        Mockito.when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                albumService.edit(albumDto));
    }
}