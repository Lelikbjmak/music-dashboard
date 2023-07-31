package com.innowise.soundfilemicroservice.dto;

import java.io.InputStream;

public record DownloadFileDto (
        String fileName,
        InputStream content
){
}
