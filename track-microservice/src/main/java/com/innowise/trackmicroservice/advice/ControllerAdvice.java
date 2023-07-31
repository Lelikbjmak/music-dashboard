package com.innowise.trackmicroservice.advice;

import com.innowise.trackmicroservice.controller.AlbumController;
import com.innowise.trackmicroservice.controller.ArtistController;
import com.innowise.trackmicroservice.controller.TrackController;
import com.innowise.trackmicroservice.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        AlbumController.class,
        ArtistController.class,
        TrackController.class
})
public class ControllerAdvice {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> resourceNotFoundExceptionHandling(ResourceNotFoundException exception) {
        log.debug("Exception ResourceNotFoundException has occurred. Message: `{}`", exception.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> methodArgumentNotValidExceptionHandling(MethodArgumentNotValidException exception) {
        log.error("Exception MethodArgumentNotValidException has occurred. Message: `{}`", exception.getMessage());
        Map<String, Object> errors = extractConstraintsViolations(exception);
        return ResponseEntity.badRequest().body(errors);
    }

    private Map<String, Object> extractConstraintsViolations(MethodArgumentNotValidException exception) {
        Map<String, Object> handledErrors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String errorPlace = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            handledErrors.put(errorPlace, errorMessage);
        });
        return handledErrors;
    }
}
