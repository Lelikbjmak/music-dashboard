package com.innowise.soundfilemicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.camelcommon.exception.AwsServiceUnavailableException;
import com.innowise.soundfilemicroservice.domain.domainenum.StorageTypeEnum;
import com.innowise.soundfilemicroservice.dto.DownloadFileDto;
import com.innowise.soundfilemicroservice.dto.UploadedAudioFileDto;
import com.innowise.soundfilemicroservice.exception.AudioFileNotFoundException;
import com.innowise.soundfilemicroservice.service.FileService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "unit")
@WebMvcTest(controllers = SoundFileController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SoundFileControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FileService fileService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(fileService);
    }

    @Test
    @WithMockUser
    void whenValidHttp_thenUploadFileMustReturn200() throws Exception {

        final String fileName = "testFile";
        final String fileContent = "Test file content...";

        MockMultipartFile file = new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, fileContent.getBytes());

        UploadedAudioFileDto mockUploadedFileDto = new UploadedAudioFileDto(
                1,
                fileName,
                StorageTypeEnum.S3,
                "bucket/" + fileName,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Mockito.when(fileService.uploadFile(file)).thenReturn(mockUploadedFileDto);

        String actualUploadedFileDtoJson = mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/sound-file")
                        .file(file)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UploadedAudioFileDto actualUploadedFileDto = objectMapper.readValue(actualUploadedFileDtoJson,
                UploadedAudioFileDto.class);

        Assertions.assertNotNull(actualUploadedFileDto);
        Assertions.assertEquals(mockUploadedFileDto, actualUploadedFileDto);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenUploadFileMustCallBusinessLogicAndReturn200() throws Exception {

        final String fileName = "testFile";
        final String fileContent = "Test file content...";

        MockMultipartFile file = new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, fileContent.getBytes());

        Mockito.when(fileService.uploadFile(file)).thenReturn(Mockito.any());

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/sound-file")
                        .file(file)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<MultipartFile> fileArgumentCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        Mockito.verify(fileService, Mockito.times(1)).uploadFile(fileArgumentCaptor.capture());
        assertThat(fileArgumentCaptor.getValue().getOriginalFilename()).isEqualTo(fileName);
        assertThat(fileArgumentCaptor.getValue().getBytes()).isEqualTo(fileContent.getBytes());
    }

    @Test
    void whenNotAuthenticated_thenUploadFileMustReturn401() throws Exception {

        final String fileName = "testFile";
        final String fileContent = "Test file content...";

        MockMultipartFile file = new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, fileContent.getBytes());

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/sound-file")
                        .file(file)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void whenValidHttp_thenDownloadFileMustReturn200() throws Exception {

        final String fileName = "testFile";
        final String fileContent = "Test content...";

        DownloadFileDto downloadFileDto = new DownloadFileDto(
                fileName,
                new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8))
        );

        Mockito.when(fileService.downloadFile(1)).thenReturn(downloadFileDto);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(downloadFileDto.fileName())
                .build();

        byte[] actualContent = mockMvc.perform(get("/api/v1/sound-file/{id}", 1)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString()))
                .andReturn().getResponse().getContentAsByteArray();

        Assertions.assertNotNull(actualContent);
        Assertions.assertArrayEquals(fileContent.getBytes(), actualContent);
    }

    @Test
    @WithMockUser
    void whenFileNotExists_thenDownloadFileMustReturn404() throws Exception {

        Mockito.when(fileService.downloadFile(1)).thenThrow(new AudioFileNotFoundException("File not found."));

        mockMvc.perform(get("/api/v1/sound-file/{id}", 1)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void whenServiceNotResponse_thenDownloadFileMustReturn503() throws Exception {

        Mockito.when(fileService.downloadFile(1)).thenThrow(new AwsServiceUnavailableException("Service is temporary unavailable."));

        mockMvc.perform(get("/api/v1/sound-file/{id}", 1)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void whenNotAuthenticated_thenDownloadFileMustReturn401() throws Exception {

        mockMvc.perform(get("/api/v1/sound-file/{id}", 1)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}