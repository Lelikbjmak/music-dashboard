package com.innowise.camelcommon.dto;

public record UploadedFileDto(
        String fileName,

        String path,

        String storage
) {
}
