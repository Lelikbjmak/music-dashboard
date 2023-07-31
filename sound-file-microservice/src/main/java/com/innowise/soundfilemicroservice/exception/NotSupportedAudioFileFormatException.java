package com.innowise.soundfilemicroservice.exception;

public class NotSupportedAudioFileFormatException extends RuntimeException {

    public NotSupportedAudioFileFormatException(String message) {
        super(message);
    }

}
