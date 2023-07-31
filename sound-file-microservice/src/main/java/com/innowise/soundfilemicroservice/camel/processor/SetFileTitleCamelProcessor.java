package com.innowise.soundfilemicroservice.camel.processor;

import com.innowise.soundfilemicroservice.util.Mp3TrackTitleParserUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.web.multipart.MultipartFile;

public class SetFileTitleCamelProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        final String fileNameHeader = "fileName";
        MultipartFile file = exchange.getIn().getBody(MultipartFile.class);
        String fileTittle = Mp3TrackTitleParserUtil.parseTrackTitle(file.getInputStream());

        exchange.getIn().setHeader(fileNameHeader, fileTittle);
    }
}
