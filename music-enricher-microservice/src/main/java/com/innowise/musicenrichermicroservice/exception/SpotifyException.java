package com.innowise.musicenrichermicroservice.exception;

import lombok.Getter;

@Getter
public class SpotifyException extends RuntimeException {


    public SpotifyException() {
        super();
    }

    public SpotifyException(String message) {
        super(message);
    }


    public SpotifyException(String message, Throwable cause) {
        super(message, cause);
    }


    public SpotifyException(Throwable cause) {
        super(cause);
    }

}
