package com.innowise.musicenrichermicroservice.util;

import com.innowise.musicenrichermicroservice.dto.EnrichTrackDto;
import com.innowise.musicenrichermicroservice.exception.NotSupportedAudioFileFormatException;
import lombok.experimental.UtilityClass;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import static com.innowise.musicenrichermicroservice.constant.FileMetadataConstant.TRACK_CREATOR_METADATA_PROPERTY;
import static com.innowise.musicenrichermicroservice.constant.FileMetadataConstant.TRACK_TITLE_METADATA_PROPERTY;

@UtilityClass
public class Mp3TrackMetadataParserUtil {

    public static EnrichTrackDto extractRowTrackMetadata(InputStream trackAudioFile) {

        try {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();
            Parser parser = new Mp3Parser();
            parser.parse(trackAudioFile, handler, metadata, parseContext);

            String title = metadata.get(TRACK_TITLE_METADATA_PROPERTY);
            String trackArtists = metadata.get(TRACK_CREATOR_METADATA_PROPERTY);
            String creator = trackArtists.split(",")[0];
            return new EnrichTrackDto(title, creator);
        } catch (NullPointerException | TikaException | IOException | SAXException e) {
            throw new NotSupportedAudioFileFormatException(e);
        }
    }

}
