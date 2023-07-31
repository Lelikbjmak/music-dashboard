package com.innowise.soundfilemicroservice.repository;

import com.innowise.soundfilemicroservice.domain.UploadedAudioFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UploadedAudioFileRepository extends JpaRepository<UploadedAudioFile, Long> {

    @Query(value = "SELECT file FROM UploadedAudioFile file where file.fileName = :name")
    Optional<UploadedAudioFile> findByName(@Param(value = "name") String fileName);
}
