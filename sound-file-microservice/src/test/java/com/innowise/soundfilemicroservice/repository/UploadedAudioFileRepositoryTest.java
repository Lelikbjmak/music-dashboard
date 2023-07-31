package com.innowise.soundfilemicroservice.repository;

import com.innowise.soundfilemicroservice.domain.UploadedAudioFile;
import com.innowise.soundfilemicroservice.domain.domainenum.StorageTypeEnum;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles(value = "jpa")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UploadedAudioFileRepositoryTest {

    @Autowired
    private UploadedAudioFileRepository uploadedAudioFileRepository;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(uploadedAudioFileRepository);
    }

    @Test
    @Order(2)
    void mustSaveAudioFile(@Value(value = "${audio-file.name}") String fileName) {
        UploadedAudioFile uploadedAudioFile = UploadedAudioFile.builder()
                .fileName(fileName)
                .storage(StorageTypeEnum.S3)
                .path("test-path")
                .build();

        UploadedAudioFile savedUploadedFile = uploadedAudioFileRepository.save(uploadedAudioFile);
        Assertions.assertNotNull(savedUploadedFile);
        Assertions.assertNotEquals(0, savedUploadedFile.getId());
    }

    @Test
    @Order(3)
    void mustNotSaveAudioFileNullableFiled(@Value(value = "${audio-file.name}") String fileName) {
        UploadedAudioFile uploadedAudioFile = UploadedAudioFile.builder()
                .build();

        Exception exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                uploadedAudioFileRepository.save(uploadedAudioFile));

        Assertions.assertEquals("not-null property references a null or transient value : com.innowise.soundfilemicroservice.domain.UploadedAudioFile.fileName", exception.getMessage()); // should be not null
    }

    @Test
    @Order(4)
    @Sql(value = "/sql/01-create-uploaded-files.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/02-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustThrowErrorNotUniqueFileName(@Value(value = "${audio-file.name}") String fileName) {
        UploadedAudioFile uploadedAudioFile = UploadedAudioFile.builder()
                .fileName(fileName)
                .storage(StorageTypeEnum.S3)
                .path("test-path")
                .build();

        Exception exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                uploadedAudioFileRepository.save(uploadedAudioFile));

        System.out.println(exception.getMessage());

        Assertions.assertTrue(exception.getMessage().contains("uc_uploaded_audio_files_filename"));
    }

    @Test
    @Order(5)
    @Sql(value = "/sql/01-create-uploaded-files.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/02-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustReturnUploadedAudioFileById() {
        Optional<UploadedAudioFile> optionalUploadedAudioFile = uploadedAudioFileRepository.findById(1L);
        Assertions.assertTrue(optionalUploadedAudioFile.isPresent());
    }

    @Test
    @Order(6)
    void mustReturnEmptyUploadedAudioFileById() {
        Optional<UploadedAudioFile> optionalUploadedAudioFile = uploadedAudioFileRepository.findById(1L);
        Assertions.assertTrue(optionalUploadedAudioFile.isEmpty());
    }

    @Test
    @Order(7)
    @Sql(value = "/sql/01-create-uploaded-files.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void mustDeleteAudioFileById() {
        final long id = 1;
        Optional<UploadedAudioFile> optionalUploadedAudioFile = uploadedAudioFileRepository.findById(id);
        Assertions.assertTrue(optionalUploadedAudioFile.isPresent());

        uploadedAudioFileRepository.deleteById(id);

        Optional<UploadedAudioFile> deletedAudioFile = uploadedAudioFileRepository.findById(id);
        Assertions.assertTrue(deletedAudioFile.isEmpty());
    }
}