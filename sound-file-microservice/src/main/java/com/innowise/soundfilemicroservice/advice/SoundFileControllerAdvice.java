package com.innowise.soundfilemicroservice.advice;

import com.innowise.camelcommon.exception.AwsServiceUnavailableException;
import com.innowise.soundfilemicroservice.controller.SoundFileController;
import com.innowise.soundfilemicroservice.exception.AudioFileNotFoundException;
import com.innowise.soundfilemicroservice.exception.NotSupportedAudioFileFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = SoundFileController.class)
public class SoundFileControllerAdvice {

    @ExceptionHandler(value = AudioFileNotFoundException.class)
    public ResponseEntity<Object> fileNotFoundHandler(AudioFileNotFoundException exception) {
        log.debug("Exception AudioFileNotFoundException has occurred. Message: `{}`", exception.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = AwsServiceUnavailableException.class)
    public ResponseEntity<Object> serviceNotAvailableHandler(AwsServiceUnavailableException exception) {
        log.debug("Exception AwsServiceUnavailableException has occurred. Message: `{}`", exception.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exception.getMessage());
    }

    @ExceptionHandler(value = NotSupportedAudioFileFormatException.class)
    public ResponseEntity<Object> serviceNotAvailableHandler(NotSupportedAudioFileFormatException exception) {
        log.debug("Exception NotSupportedAudioFileFormatException has occurred. Message: `{}`", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
