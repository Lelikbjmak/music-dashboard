package com.innowise.trackmicroservice.service;

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
import com.innowise.trackmicroservice.service.impl.TrackServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrackServiceUnitTest {

    @Mock
    private TrackMapper trackMapper;

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private TrackServiceImpl trackService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(trackService);
        Assertions.assertNotNull(trackMapper);
        Assertions.assertNotNull(artistRepository);
        Assertions.assertNotNull(trackRepository);
        Assertions.assertNotNull(albumRepository);
    }

    @Test
    void mustRegisterNewTrack() {
        final String mockTrackId = "mockTrackId";
        final String mockArtistId1 = "mockArtistId1";
        final String mockArtistId2 = "mockArtistId2";
        final String mockAlbumId = "mockAlbumId";

        List<String> mockTrackArtistIdList = List.of(mockArtistId1, mockArtistId2);

        SpotifyTrackDto mockSpotifyTrackDto = new SpotifyTrackDto(
                mockTrackId,
                "title",
                1,
                19,
                10,
                2963,
                "spotifyUri",
                "spotifyUri",
                mockAlbumId,
                mockTrackArtistIdList,
                mockTrackArtistIdList
        );

        List<Artist> mockArtistList = List.of(
                Artist.builder()
                        .id(mockArtistId1)
                        .build(),
                Artist.builder()
                        .id(mockArtistId2)
                        .build()
        );

        Album mockAlbum = Album.builder()
                .id(mockAlbumId)
                .build();

        Track mockTrack = Track.builder()
                .id(mockTrackId)
                .title("title")
                .durationMs(2963)
                .popularity(10)
                .discNumber(1)
                .trackNumber(19)
                .spotifyUri("spotifyUri")
                .build();

        TrackDto trackDto = new TrackDto(
                mockTrackId,
                "title",
                1,
                2963L,
                "spotifyUri",
                "spotifyUri",
                19,
                10,
                null,
                null
        );

        Mockito.when(artistRepository.findByIdList(mockTrackArtistIdList)).thenReturn(mockArtistList);
        Mockito.when(albumRepository.findById(mockAlbumId)).thenReturn(Optional.of(mockAlbum));
        Mockito.when(trackMapper.mapToEntity(mockSpotifyTrackDto)).thenReturn(mockTrack);

        mockTrack.setAlbum(mockAlbum);
        mockTrack.setArtistList(mockArtistList);

        Mockito.when(trackRepository.save(mockTrack)).thenReturn(mockTrack);
        Mockito.when(trackMapper.mapToDto(mockTrack)).thenReturn(trackDto);

        TrackDto actualTrackDto = trackService.registerNewTrack(mockSpotifyTrackDto);

        Assertions.assertNotNull(actualTrackDto);
    }

    @Test
    void mustSaveTrack() {
        TrackDto mockTrackDto = new TrackDto(
                "trackId",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Track mockTrack = Track.builder()
                .id("trackId")
                .build();

        Mockito.when(trackMapper.mapToEntity(mockTrackDto)).thenReturn(mockTrack);
        Mockito.when(trackRepository.save(mockTrack)).thenReturn(mockTrack);
        Mockito.when(trackMapper.mapToDto(mockTrack)).thenReturn(mockTrackDto);

        TrackDto mockSavedTrackDto = trackService.save(mockTrackDto);

        Assertions.assertNotNull(mockSavedTrackDto);
    }

    @Test
    void mustDeleteTrackById() {
        final String mockTrackId = "mockTrackId";
        Track mockTrack = new Track();
        Mockito.when(trackRepository.findById(mockTrackId)).thenReturn(Optional.of(mockTrack));

        trackService.delete(mockTrackId);
    }

    @Test
    void mustDeleteTrackById_trackNotFound() {
        final String mockTrackId = "mockTrackId";
        Mockito.when(trackRepository.findById(mockTrackId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                trackService.delete(mockTrackId));
    }

    @Test
    void mustEditTrackById() {
        final String mockTrackToEditId = "mockId";
        TrackDto mockTrackDto = new TrackDto(
                mockTrackToEditId,
                "oldTitle",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        TrackDto mockTrackDtoAfterEdit = new TrackDto(
                mockTrackToEditId,
                "newTitle",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Track mockTrack = Track.builder()
                .id(mockTrackToEditId)
                .title("oldTitle")
                .build();

        Track mockTrackAfterEdit = Track.builder()
                .id(mockTrackToEditId)
                .title("oldTitle")
                .build();

        Mockito.when(trackRepository.findById(mockTrackToEditId)).thenReturn(Optional.of(mockTrack));
        Mockito.when(trackRepository.save(mockTrack)).thenReturn(mockTrackAfterEdit);
        Mockito.when(trackMapper.mapToDto(mockTrackAfterEdit)).thenReturn(mockTrackDtoAfterEdit);

        TrackDto actualTrackDto = trackService.edit(mockTrackDto);
        Assertions.assertNotNull(actualTrackDto);
        Assertions.assertEquals(mockTrackDtoAfterEdit, actualTrackDto);
    }

    @Test
    @WithMockUser
    void mustEditTrackById_trackNotFound() {
        final String mockTrackId = "id";
        TrackDto mockTrackDto = new TrackDto(
                mockTrackId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Mockito.when(trackRepository.findById(mockTrackId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                trackService.edit(mockTrackDto));
    }

    @Test
    void musFindTrackById() {
        final String mockId = "mockId";
        Track mockTrack = Track.builder()
                .id(mockId)
                .build();

        TrackDto mockTrackDto = new TrackDto(
                mockId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Mockito.when(trackRepository.findById(mockId)).thenReturn(Optional.of(mockTrack));
        Mockito.when(trackMapper.mapToDto(mockTrack)).thenReturn(mockTrackDto);
        TrackDto actualTrackDto = trackService.findById(mockId);

        Assertions.assertNotNull(actualTrackDto);
        Assertions.assertEquals(mockTrackDto, actualTrackDto);
    }
}