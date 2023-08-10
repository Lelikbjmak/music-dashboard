package com.innowise.musicenrichermicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.musicenrichermicroservice.service.EnrichService;
import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import com.innowise.spotifycommon.dto.enumeration.AlbumTypeEnum;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "unit")
@WebMvcTest(controllers = EnrichController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EnrichControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnrichService enrichService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(enrichService);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenEnrichAlbumByIdMustReturn200() throws Exception {
        final String albumId = "testAlbumId";

        final SpotifyAlbumDto mockSpotifyAlbumDto = new SpotifyAlbumDto(
                albumId,
                "testAlbum",
                AlbumTypeEnum.ALBUM,
                "Worldwide Baby",
                100,
                13,
                null,
                "www.spotify.com/test/spotify/album/url.com",
                "www.spotify.com/test/spotify/album/url.com",
                List.of("Artist1", "Artist2")
        );

        Mockito.when(enrichService.enrichAlbumMetadataWithSpotify(albumId)).thenReturn(mockSpotifyAlbumDto);

        String jsonResponseBody = mockMvc.perform(get("/api/v1/enrich/albums/{id}", albumId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertNotNull(jsonResponseBody);

        SpotifyAlbumDto actulSpotifyAlbumDto = objectMapper.readValue(jsonResponseBody, SpotifyAlbumDto.class);
        Assertions.assertEquals(mockSpotifyAlbumDto, actulSpotifyAlbumDto);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenEnrichAlbumByIdMustCallBusinessLogicReturn200() throws Exception {
        final String albumId = "testAlbumId";

        Mockito.when(enrichService.enrichAlbumMetadataWithSpotify(albumId)).thenReturn(Mockito.any(SpotifyAlbumDto.class));

        mockMvc.perform(get("/api/v1/enrich/albums/{id}", albumId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<String> authenticationRequestArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(enrichService, Mockito.times(1))
                .enrichAlbumMetadataWithSpotify(authenticationRequestArgumentCaptor.capture());

        assertThat(authenticationRequestArgumentCaptor.getValue()).isEqualTo(albumId);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenEnrichArtistByIdMustReturn200() throws Exception {
        final String artistId = "testArtistId";

        final SpotifyArtistDto mockSpotifyArtistDto = new SpotifyArtistDto(
                artistId,
                "artistName",
                100,
                Set.of("ROCK"),
                "www.spotify.com/path/to/artist",
                "www.spotify.com/path/to/artist"
        );

        Mockito.when(enrichService.enrichArtistMetadataWithSpotify(artistId)).thenReturn(mockSpotifyArtistDto);

        String jsonResponseBody = mockMvc.perform(get("/api/v1/enrich/artists/{id}", artistId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertNotNull(jsonResponseBody);

        SpotifyArtistDto actualSpotifyArtistDto = objectMapper.readValue(jsonResponseBody, SpotifyArtistDto.class);
        Assertions.assertEquals(mockSpotifyArtistDto, actualSpotifyArtistDto);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenEnrichArtistByIdMustCallBusinessLogicReturn200() throws Exception {
        final String artistId = "testArtistId";

        Mockito.when(enrichService.enrichArtistMetadataWithSpotify(artistId)).thenReturn(Mockito.any(SpotifyArtistDto.class));

        mockMvc.perform(get("/api/v1/enrich/artists/{id}", artistId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<String> authenticationRequestArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(enrichService, Mockito.times(1))
                .enrichArtistMetadataWithSpotify(authenticationRequestArgumentCaptor.capture());

        assertThat(authenticationRequestArgumentCaptor.getValue()).isEqualTo(artistId);
    }
}