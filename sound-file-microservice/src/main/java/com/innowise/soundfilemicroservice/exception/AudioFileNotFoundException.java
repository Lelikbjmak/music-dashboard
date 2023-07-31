package com.innowise.soundfilemicroservice.exception;

public class AudioFileNotFoundException extends RuntimeException {

    public AudioFileNotFoundException(String message) {
        super(message);
    }

}
