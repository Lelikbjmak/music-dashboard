package com.innowise.soundfilemicroservice.service;

import com.innowise.camelcommon.dto.UploadedFileDto;
import com.innowise.soundfilemicroservice.domain.UploadedAudioFile;
import com.innowise.soundfilemicroservice.domain.domainenum.StorageTypeEnum;
import com.innowise.soundfilemicroservice.dto.DownloadFileDto;
import com.innowise.soundfilemicroservice.dto.UploadedAudioFileDto;
import com.innowise.soundfilemicroservice.exception.AudioFileNotFoundException;
import com.innowise.soundfilemicroservice.mapper.UploadedAudioFileMapper;
import com.innowise.soundfilemicroservice.repository.UploadedAudioFileRepository;
import com.innowise.soundfilemicroservice.service.impl.FileServiceImpl;
import com.innowise.soundfilemicroservice.util.Mp3TrackTitleParserUtil;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.innowise.soundfilemicroservice.constant.CamelConstant.*;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class FileServiceUnitTest {

    @Mock
    private UploadedAudioFileRepository uploadedAudioFileRepository;

    @Mock
    private UploadedAudioFileMapper uploadedAudioFileMapper;

    @Mock
    private ProducerTemplate producerTemplate;

    @InjectMocks
    private FileServiceImpl fileStoreService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(uploadedAudioFileRepository);
        Assertions.assertNotNull(uploadedAudioFileMapper);
        Assertions.assertNotNull(producerTemplate);
        Assertions.assertNotNull(fileStoreService);
    }

    @Test
    @Order(2)
    void mustUploadFile() throws IOException {

        File audioFile = new File("../test-files/Jemi.mp3");
        final String fileName = Mp3TrackTitleParserUtil.parseTrackTitle(new FileInputStream(audioFile));

        MultipartFile file = new MockMultipartFile(fileName, fileName, null, new FileInputStream(audioFile));

        UploadedAudioFile mockUploadedFile = UploadedAudioFile.builder()
                .fileName("testFile")
                .storage(StorageTypeEnum.S3)
                .build();

        UploadedAudioFile mockSavedUploadedFile = UploadedAudioFile.builder()
                .id(1)
                .fileName("testFile")
                .storage(StorageTypeEnum.S3)
                .build();

        UploadedAudioFileDto mockSavedFileDto = new UploadedAudioFileDto(
                1,
                "testFile",
                StorageTypeEnum.S3,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        UploadedFileDto mockRequestToSQSFile = new UploadedFileDto("testFile", "s3/testFile", "S3");

        Mockito.when(producerTemplate.requestBody(UPLOAD_S3_ROUTE,
                file, UploadedAudioFile.class)).thenReturn(mockUploadedFile);
        Mockito.when(uploadedAudioFileRepository.save(mockUploadedFile)).thenReturn(mockSavedUploadedFile);
        Mockito.when(uploadedAudioFileMapper.mapToDto(mockSavedUploadedFile)).thenReturn(mockSavedFileDto);
        Mockito.when(uploadedAudioFileMapper.mapToSqsDto(mockSavedUploadedFile)).thenReturn(mockRequestToSQSFile);
        Mockito.doNothing().when(producerTemplate).sendBody(UPLOAD_TO_SQS_ROUTE, mockRequestToSQSFile);

        UploadedAudioFileDto uploadedFile = fileStoreService.uploadFile(file);
        Assertions.assertEquals(mockSavedFileDto, uploadedFile);

        Mockito.verify(uploadedAudioFileRepository, Mockito.times(1)).save(mockUploadedFile);
        Mockito.verify(uploadedAudioFileMapper, Mockito.times(1)).mapToSqsDto(mockSavedUploadedFile);
        Mockito.verify(uploadedAudioFileMapper, Mockito.times(1)).mapToDto(mockSavedUploadedFile);
        Mockito.verify(producerTemplate, Mockito.times(1)).sendBody(UPLOAD_TO_SQS_ROUTE, mockRequestToSQSFile);
        Mockito.verify(producerTemplate, Mockito.times(1)).requestBody(UPLOAD_S3_ROUTE,
                file, UploadedAudioFile.class);
    }

    @Test
    @Order(3)
    void mustDownloadFile() {

        final long id = 1;
        final String fileName = "testFile";
        final String path = "music-file-bucket/testFile";

        final String content = "Content...";
        InputStream contentStream = new ByteArrayInputStream(content.getBytes());

        DownloadFileDto expectedDownloadedFileDto = new DownloadFileDto(
                fileName,
                contentStream
        );

        UploadedAudioFile mockDownloadedFile = UploadedAudioFile.builder()
                .id(id)
                .fileName(fileName)
                .path(path)
                .uploadedDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .storage(StorageTypeEnum.S3)
                .build();

        UploadedAudioFileDto mockDownloadedFileDto = new UploadedAudioFileDto(
                id,
                fileName,
                StorageTypeEnum.S3,
                path,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Mockito.when(uploadedAudioFileRepository.findById(id)).thenReturn(Optional.of(mockDownloadedFile));
        Mockito.when(uploadedAudioFileMapper.mapToDto(mockDownloadedFile)).thenReturn(mockDownloadedFileDto);
        Mockito.when(producerTemplate.requestBody(DOWNLOAD_FILE_ROUTE,
                mockDownloadedFileDto, InputStream.class)).thenReturn(contentStream);

        DownloadFileDto actualDownloadedFileDto = fileStoreService.downloadFile(id);

        Mockito.verify(uploadedAudioFileRepository, Mockito.times(1)).findById(id);
        Mockito.verify(uploadedAudioFileMapper, Mockito.times(1)).mapToDto(mockDownloadedFile);
        Mockito.verify(producerTemplate, Mockito.times(1)).requestBody(DOWNLOAD_FILE_ROUTE,
                mockDownloadedFileDto, InputStream.class);

        Assertions.assertNotNull(actualDownloadedFileDto);
        Assertions.assertEquals(expectedDownloadedFileDto, actualDownloadedFileDto);
    }

    @Test
    @Order(3)
    void mustThrowErrorFileNotFound() {

        final long id = 1;
        Mockito.when(uploadedAudioFileRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(AudioFileNotFoundException.class, () ->
                fileStoreService.downloadFile(id));

        Mockito.verify(uploadedAudioFileRepository, Mockito.times(1)).findById(id);

        Assertions.assertEquals("File ID: " + id + " is not found.", exception.getMessage());
    }
}