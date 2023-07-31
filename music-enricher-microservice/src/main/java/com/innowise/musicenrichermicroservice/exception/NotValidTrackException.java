package com.innowise.musicenrichermicroservice.exception;

public class NotValidTrackException extends RuntimeException {

    public NotValidTrackException() {
        super();
    }

    public NotValidTrackException(Throwable throwable) {
        super(throwable);
    }

    public NotValidTrackException(String message) {
        super(message);
    }
}
