package com.innowise.soundfilemicroservice.service;

import com.innowise.soundfilemicroservice.dto.DownloadFileDto;
import com.innowise.soundfilemicroservice.dto.UploadedAudioFileDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileService {

    UploadedAudioFileDto uploadFile(MultipartFile file) throws IOException;

    DownloadFileDto downloadFile(long id) throws IOException;
}
