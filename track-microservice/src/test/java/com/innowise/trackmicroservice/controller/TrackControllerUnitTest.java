package com.innowise.trackmicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.trackmicroservice.dto.TrackDto;
import com.innowise.trackmicroservice.service.TrackService;
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

@WebMvcTest(controllers = TrackController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrackControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrackService trackService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(trackService);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenGetTrackByIdMustReturn200() throws Exception {
        final String trackId = "trackId";

        TrackDto trackDto = new TrackDto(
                trackId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Mockito.when(trackService.findById(trackId)).thenReturn(trackDto);

        String jsonResponseBody = mockMvc.perform(get("/api/v1/tracks/{id}", trackId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertNotNull(jsonResponseBody);

        TrackDto actualTrackDto = objectMapper.readValue(jsonResponseBody, TrackDto.class);
        Assertions.assertEquals(trackDto, actualTrackDto);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenGetTrackByIdMustCallBusinessLogicAndReturn200() throws Exception {
        final String trackId = "trackId";

        Mockito.when(trackService.findById(trackId)).thenReturn(Mockito.any(TrackDto.class));

        mockMvc.perform(get("/api/v1/tracks/{id}", trackId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<String> trackIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(trackService, Mockito.times(1))
                .findById(trackIdArgumentCaptor.capture());

        assertThat(trackIdArgumentCaptor.getValue()).isEqualTo(trackId);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenDeleteTrackByIdMustReturn200() throws Exception {
        final String albumId = "albumId";

        mockMvc.perform(delete("/api/v1/tracks/{id}", albumId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void whenValidInput_thenDeleteTrackByIdMustCallBusinessLogicAndReturn200() throws Exception {
        final String trackId = "trackId";

        mockMvc.perform(delete("/api/v1/tracks/{id}", trackId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<String> trackIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(trackService, Mockito.times(1))
                .delete(trackIdArgumentCaptor.capture());

        assertThat(trackIdArgumentCaptor.getValue()).isEqualTo(trackId);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenEditTrackByIdMustReturn200() throws Exception {
        final String trackId = "trackId";

        TrackDto trackDto = new TrackDto(
                trackId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );


        TrackDto mockTrackDto = new TrackDto(
                trackId,
                "newTitle",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );


        Mockito.when(trackService.edit(trackDto)).thenReturn(mockTrackDto);

        String jsonResponseBody = mockMvc.perform(put("/api/v1/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trackDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TrackDto actualTrackDto = objectMapper.readValue(jsonResponseBody, TrackDto.class);

        Assertions.assertNotNull(actualTrackDto);
        Assertions.assertEquals(mockTrackDto, actualTrackDto);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenEditTrackByIdMustCallBusinessLogicAndReturn200() throws Exception {
        final String trackId = "trackId";
        final String trackName = "trackName";

        TrackDto trackDto = new TrackDto(
                trackId,
                trackName,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Mockito.when(trackService.edit(trackDto)).thenReturn(Mockito.any(TrackDto.class));

        mockMvc.perform(put("/api/v1/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trackDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<TrackDto> trackDtoArgumentCaptor = ArgumentCaptor.forClass(TrackDto.class);
        Mockito.verify(trackService, Mockito.times(1))
                .edit(trackDtoArgumentCaptor.capture());

        assertThat(trackDtoArgumentCaptor.getValue().id()).isEqualTo(trackId);
        assertThat(trackDtoArgumentCaptor.getValue().title()).isEqualTo(trackName);
    }
}