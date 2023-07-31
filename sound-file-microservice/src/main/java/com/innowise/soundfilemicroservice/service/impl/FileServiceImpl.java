package com.innowise.soundfilemicroservice.service.impl;

import com.innowise.camelcommon.dto.UploadedFileDto;
import com.innowise.soundfilemicroservice.domain.UploadedAudioFile;
import com.innowise.soundfilemicroservice.dto.DownloadFileDto;
import com.innowise.soundfilemicroservice.dto.UploadedAudioFileDto;
import com.innowise.soundfilemicroservice.exception.AudioFileNotFoundException;
import com.innowise.soundfilemicroservice.mapper.UploadedAudioFileMapper;
import com.innowise.soundfilemicroservice.repository.UploadedAudioFileRepository;
import com.innowise.soundfilemicroservice.service.FileService;
import com.innowise.soundfilemicroservice.util.Mp3TrackTitleParserUtil;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static com.innowise.soundfilemicroservice.constant.CamelConstant.*;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final UploadedAudioFileRepository uploadedAudioFileRepository;

    private final UploadedAudioFileMapper uploadedAudioFileMapper;

    private final ProducerTemplate producerTemplate;

    @Override
    @Transactional
    public UploadedAudioFileDto uploadFile(MultipartFile file) throws IOException {

        String trackTitle = Mp3TrackTitleParserUtil.parseTrackTitle(file.getInputStream());
        Optional<UploadedAudioFile> optionalUploadedAudioFile = uploadedAudioFileRepository.findByName(trackTitle);

        if (optionalUploadedAudioFile.isPresent()) {
            return uploadedAudioFileMapper.mapToDto(optionalUploadedAudioFile.get());
        }

        UploadedAudioFile uploadedAudioFile = producerTemplate.requestBody(UPLOAD_S3_ROUTE,
                file, UploadedAudioFile.class);

        UploadedAudioFile newAudioFile = uploadedAudioFileRepository.save(uploadedAudioFile);
        UploadedFileDto sqsRequestToEnrichFile = uploadedAudioFileMapper.mapToSqsDto(newAudioFile);

        producerTemplate.sendBody(UPLOAD_TO_SQS_ROUTE, sqsRequestToEnrichFile);

        return uploadedAudioFileMapper.mapToDto(newAudioFile);
    }

    @Override
    @Transactional(readOnly = true)
    public DownloadFileDto downloadFile(long id) {

        UploadedAudioFile requestedFile = uploadedAudioFileRepository.findById(id).orElseThrow(() ->
                new AudioFileNotFoundException("File ID: " + id + " is not found."));

        UploadedAudioFileDto requestedFileDto = uploadedAudioFileMapper.mapToDto(requestedFile);

        InputStream content = producerTemplate.requestBody(DOWNLOAD_FILE_ROUTE,
                requestedFileDto, InputStream.class);

        return new DownloadFileDto(requestedFileDto.fileName(), content);
    }
}
