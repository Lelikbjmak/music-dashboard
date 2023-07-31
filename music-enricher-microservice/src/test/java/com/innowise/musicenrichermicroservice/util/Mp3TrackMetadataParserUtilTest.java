package com.innowise.musicenrichermicroservice.util;

import com.innowise.musicenrichermicroservice.dto.EnrichTrackDto;
import com.innowise.musicenrichermicroservice.exception.NotSupportedAudioFileFormatException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

class Mp3TrackMetadataParserUtilTest {

    private static final String TEST_FILE_DESTINATION = "../test-files/Jemi.mp3";

    @Test
    void mustExtractRowTrackMetadata() {
        File file = new File(TEST_FILE_DESTINATION);
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());

        try (InputStream stream = new FileInputStream(file)) {
            EnrichTrackDto enrichTrackDto = Mp3TrackMetadataParserUtil.extractRowTrackMetadata(stream);
            Assertions.assertNotNull(enrichTrackDto);
            Assertions.assertNotNull(enrichTrackDto.creator());
            Assertions.assertNotNull(enrichTrackDto.title());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void mustThrowExceptionDueToNotSupportedFileType() {
        String content = "Content...";

        try (InputStream stream = new ByteArrayInputStream(content.getBytes())) {

            Assertions.assertThrows(NotSupportedAudioFileFormatException.class, () ->
                    Mp3TrackMetadataParserUtil.extractRowTrackMetadata(stream));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}