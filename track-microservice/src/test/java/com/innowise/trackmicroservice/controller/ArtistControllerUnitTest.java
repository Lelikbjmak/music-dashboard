package com.innowise.trackmicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.trackmicroservice.dto.ArtistDto;
import com.innowise.trackmicroservice.service.ArtistService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArtistController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArtistControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArtistService artistService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(artistService);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenGetArtistByIdMustReturn200() throws Exception {
        final String artistId = "artistId";

        ArtistDto mockArtistDto = new ArtistDto(
                artistId,
                "as",
                null,
                null,
                null
        );

        Mockito.when(artistService.findById(artistId)).thenReturn(mockArtistDto);

        String jsonResponseBody = mockMvc.perform(get("/api/v1/artists/{id}", artistId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertNotNull(jsonResponseBody);

        ArtistDto actualArtistDto = objectMapper.readValue(jsonResponseBody, ArtistDto.class);
        Assertions.assertEquals(mockArtistDto, actualArtistDto);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenGetArtistByIdMustCallBusinessLogicAndReturn200() throws Exception {
        final String artistId = "artistId";

        Mockito.when(artistService.findById(artistId)).thenReturn(Mockito.any(ArtistDto.class));

        mockMvc.perform(get("/api/v1/artists/{id}", artistId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<String> albumIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(artistService, Mockito.times(1))
                .findById(albumIdArgumentCaptor.capture());

        assertThat(albumIdArgumentCaptor.getValue()).isEqualTo(artistId);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenDeleteArtistByIdMustReturn200() throws Exception {
        final String artistId = "artistId";

        mockMvc.perform(delete("/api/v1/artists/{id}", artistId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void whenValidInput_thenDeleteArtistByIdMustCallBusinessLogicAndReturn200() throws Exception {
        final String artistId = "artistId";

        mockMvc.perform(delete("/api/v1/artists/{id}", artistId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<String> albumIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(artistService, Mockito.times(1))
                .delete(albumIdArgumentCaptor.capture());

        assertThat(albumIdArgumentCaptor.getValue()).isEqualTo(artistId);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenEditArtistByIdMustReturn200() throws Exception {
        final String artistId = "artistId";

        ArtistDto artistDto = new ArtistDto(
                artistId,
                "newName",
                null,
                null,
                null
        );

        ArtistDto mockArtistDto = new ArtistDto(
                artistId,
                "newName",
                null,
                80,
                null
        );

        Mockito.when(artistService.edit(artistDto)).thenReturn(mockArtistDto);

        String jsonResponseBody = mockMvc.perform(put("/api/v1/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artistDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ArtistDto actualArtistDto = objectMapper.readValue(jsonResponseBody, ArtistDto.class);

        Assertions.assertNotNull(actualArtistDto);
        Assertions.assertEquals(mockArtistDto, actualArtistDto);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenEditArtistByIdMustCallBusinessLogicAndReturn200() throws Exception {
        final String artistId = "artistId";
        final String artistName = "newArtistName";

        ArtistDto artistDto = new ArtistDto(
                artistId,
                artistName,
                null,
                null,
                null
        );

        Mockito.when(artistService.edit(artistDto)).thenReturn(Mockito.any(ArtistDto.class));

        mockMvc.perform(put("/api/v1/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artistDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<ArtistDto> albumDtoArgumentCaptor = ArgumentCaptor.forClass(ArtistDto.class);
        Mockito.verify(artistService, Mockito.times(1))
                .edit(albumDtoArgumentCaptor.capture());

        assertThat(albumDtoArgumentCaptor.getValue().id()).isEqualTo(artistId);
        assertThat(albumDtoArgumentCaptor.getValue().name()).isEqualTo(artistName);
    }
}