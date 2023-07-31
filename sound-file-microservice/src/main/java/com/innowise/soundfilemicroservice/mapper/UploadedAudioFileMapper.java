package com.innowise.soundfilemicroservice.mapper;


import com.innowise.camelcommon.dto.UploadedFileDto;
import com.innowise.soundfilemicroservice.domain.UploadedAudioFile;
import com.innowise.soundfilemicroservice.dto.UploadedAudioFileDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UploadedAudioFileMapper {

    UploadedAudioFileDto mapToDto(UploadedAudioFile audioFile);

    default UploadedFileDto mapToSqsDto(UploadedAudioFile audioFile) {
        if (audioFile == null || audioFile.getFileName() == null ||
                audioFile.getStorage() == null || audioFile.getPath() == null)
            return null;

        return new UploadedFileDto(audioFile.getFileName(), audioFile.getPath(), audioFile.getStorage().name());
    }

}
