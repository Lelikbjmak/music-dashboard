package com.innowise.soundfilemicroservice.camel.processor;

import com.innowise.soundfilemicroservice.domain.UploadedAudioFile;
import com.innowise.soundfilemicroservice.domain.domainenum.StorageTypeEnum;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class UploadFileCamelProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {

        String fileNameHeader = "fileName";
        String storageTypeHeader = "storage";
        String pathHeader = "path";

        String fileName = exchange.getIn().getHeader(fileNameHeader, String.class);
        StorageTypeEnum storage = exchange.getIn().getHeader(storageTypeHeader, StorageTypeEnum.class);
        String path = exchange.getIn().getHeader(pathHeader, String.class);

        UploadedAudioFile newUploadedFile = UploadedAudioFile.builder()
                .fileName(fileName)
                .storage(storage)
                .path(path)
                .build();

        exchange.getIn().setBody(newUploadedFile);
    }
}
