package com.innowise.authenticationmicroservice.advice;

import com.innowise.authenticationmicroservice.controller.AuthenticationController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = AuthenticationController.class)
public class AuthenticationControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> notValidDerivedData(MethodArgumentNotValidException exception) {
        log.debug("Exception MethodArgumentNotValidException has occurred. Message: `{}`", exception.getMessage());
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
