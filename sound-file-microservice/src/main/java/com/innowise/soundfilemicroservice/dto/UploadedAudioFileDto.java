package com.innowise.soundfilemicroservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.innowise.soundfilemicroservice.domain.domainenum.StorageTypeEnum;

import java.time.LocalDateTime;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record UploadedAudioFileDto(
        long id,
        String fileName,
        StorageTypeEnum storage,
        String path,
        LocalDateTime uploadedDate,
        LocalDateTime updatedDate
) {
}
