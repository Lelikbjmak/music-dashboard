package com.innowise.soundfilemicroservice.mapper;

import com.innowise.camelcommon.dto.UploadedFileDto;
import com.innowise.soundfilemicroservice.domain.UploadedAudioFile;
import com.innowise.soundfilemicroservice.domain.domainenum.StorageTypeEnum;
import com.innowise.soundfilemicroservice.dto.UploadedAudioFileDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UploadedAudioFileMapperTest {

    @Autowired
    private UploadedAudioFileMapper uploadedAudioFileMapper;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(uploadedAudioFileMapper);
    }

    @Test
    @Order(2)
    void mustReturnNullDto() {
        UploadedAudioFileDto dto = uploadedAudioFileMapper.mapToDto(null);
        Assertions.assertNull(dto);
    }

    @Test
    @Order(3)
    void mustReturnNullSQSDto() {
        UploadedFileDto SQSDto = uploadedAudioFileMapper.mapToSqsDto(null);
        Assertions.assertNull(SQSDto);
    }

    @Test
    @Order(4)
    void mustReturnDto() {
        UploadedAudioFile entity = UploadedAudioFile.builder()
                .id(1)
                .fileName("test")
                .path("path/test")
                .storage(StorageTypeEnum.S3)
                .updatedDate(LocalDateTime.of(2020, 1, 1, 10, 10, 10))
                .uploadedDate(LocalDateTime.of(2020, 1, 1, 10, 10, 10))
                .build();

        UploadedAudioFileDto expectedDto = new UploadedAudioFileDto(
                1,
                "test",
                StorageTypeEnum.S3,
                "path/test",
                LocalDateTime.of(2020, 1, 1, 10, 10, 10),
                LocalDateTime.of(2020, 1, 1, 10, 10, 10)
        );

        UploadedAudioFileDto actualDto = uploadedAudioFileMapper.mapToDto(entity);

        Assertions.assertNotNull(actualDto);
        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    @Order(5)
    void mustReturnSQSDto() {
        UploadedAudioFile entity = UploadedAudioFile.builder()
                .fileName("test")
                .storage(StorageTypeEnum.S3)
                .path("s3/test")
                .build();

        UploadedFileDto expectedSQSDto = new UploadedFileDto(
                "test",
                "s3/test",
                StorageTypeEnum.S3.name()
        );

        UploadedFileDto actualSQSDto = uploadedAudioFileMapper.mapToSqsDto(entity);

        Assertions.assertNotNull(actualSQSDto);
        Assertions.assertEquals(expectedSQSDto, actualSQSDto);
    }
}