package com.innowise.trackmicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.trackmicroservice.dto.AlbumDto;
import com.innowise.trackmicroservice.service.AlbumService;
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

@WebMvcTest(controllers = AlbumController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AlbumControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlbumService albumService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(albumService);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenGetAlbumByIdMustReturn200() throws Exception {
        final String albumId = "albumId";

        AlbumDto mockAlbumDto = AlbumDto.builder()
                .id(albumId)
                .name("mockName")
                .build();

        Mockito.when(albumService.findById(albumId)).thenReturn(mockAlbumDto);

        String jsonResponseBody = mockMvc.perform(get("/api/v1/albums/{id}", albumId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertNotNull(jsonResponseBody);

        AlbumDto actualAlbumDto = objectMapper.readValue(jsonResponseBody, AlbumDto.class);
        Assertions.assertEquals(mockAlbumDto, actualAlbumDto);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenGetAlbumByIdMustCallBusinessLogicAndReturn200() throws Exception {
        final String albumId = "albumId";

        Mockito.when(albumService.findById(albumId)).thenReturn(Mockito.any(AlbumDto.class));

        mockMvc.perform(get("/api/v1/albums/{id}", albumId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<String> albumIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(albumService, Mockito.times(1))
                .findById(albumIdArgumentCaptor.capture());

        assertThat(albumIdArgumentCaptor.getValue()).isEqualTo(albumId);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenDeleteAlbumByIdMustReturn200() throws Exception {
        final String albumId = "albumId";

        mockMvc.perform(delete("/api/v1/albums/{id}", albumId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void whenValidInput_thenDeleteAlbumByIdMustCallBusinessLogicAndReturn200() throws Exception {
        final String albumId = "albumId";

        mockMvc.perform(delete("/api/v1/albums/{id}", albumId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<String> albumIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(albumService, Mockito.times(1))
                .delete(albumIdArgumentCaptor.capture());

        assertThat(albumIdArgumentCaptor.getValue()).isEqualTo(albumId);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenEditAlbumByIdMustReturn200() throws Exception {
        final String albumId = "albumId";

        final AlbumDto albumDto = new AlbumDto();
        albumDto.setId(albumId);

        AlbumDto mockAlbumDto = new AlbumDto();
        mockAlbumDto.setId(albumId);
        mockAlbumDto.setName("TestName");

        Mockito.when(albumService.edit(albumDto)).thenReturn(mockAlbumDto);

        String jsonResponseBody = mockMvc.perform(put("/api/v1/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AlbumDto actualAlbumDto = objectMapper.readValue(jsonResponseBody, AlbumDto.class);

        Assertions.assertNotNull(actualAlbumDto);
        Assertions.assertEquals(mockAlbumDto, actualAlbumDto);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenEditAlbumByIdMustCallBusinessLogicAndReturn200() throws Exception {
        final String albumId = "albumId";
        final String albumName = "newAlbumName";

        final AlbumDto albumDto = new AlbumDto();
        albumDto.setId(albumId);
        albumDto.setName(albumName);

        Mockito.when(albumService.edit(albumDto)).thenReturn(Mockito.any(AlbumDto.class));

        mockMvc.perform(put("/api/v1/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<AlbumDto> albumDtoArgumentCaptor = ArgumentCaptor.forClass(AlbumDto.class);
        Mockito.verify(albumService, Mockito.times(1))
                .edit(albumDtoArgumentCaptor.capture());

        assertThat(albumDtoArgumentCaptor.getValue().getId()).isEqualTo(albumId);
        assertThat(albumDtoArgumentCaptor.getValue().getName()).isEqualTo(albumName);
    }
}