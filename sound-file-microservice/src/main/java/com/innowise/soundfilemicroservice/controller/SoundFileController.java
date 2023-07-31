package com.innowise.soundfilemicroservice.controller;

import com.innowise.soundfilemicroservice.dto.DownloadFileDto;
import com.innowise.soundfilemicroservice.dto.UploadedAudioFileDto;
import com.innowise.soundfilemicroservice.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/sound-file")
public class SoundFileController {

    private final FileService fileStoreService;

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadedAudioFileDto uploadFile(@RequestParam(name = "file") MultipartFile file) throws IOException {
        log.debug("Processing request to upload file. Name - `{}`...", file.getOriginalFilename());
        return fileStoreService.uploadFile(file);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable(name = "id") long id) throws IOException {
        log.debug("Processioning request to download file id - `{}`...", id);

        DownloadFileDto requestedAudioFile = fileStoreService.downloadFile(id);

        InputStreamResource resource = new InputStreamResource(requestedAudioFile.content());
        String encodedFilename = URLEncoder.encode(requestedAudioFile.fileName(), StandardCharsets.UTF_8.toString());

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(encodedFilename)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }

}
