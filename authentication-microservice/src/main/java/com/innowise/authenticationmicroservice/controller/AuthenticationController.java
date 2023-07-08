package com.innowise.authenticationmicroservice.controller;

import com.innowise.authenticationmicroservice.dto.AuthenticationRequest;
import com.innowise.authenticationmicroservice.dto.AuthenticationResponse;
import com.innowise.authenticationmicroservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        log.debug("Processing request to authenticate user. Username - `{}`", authenticationRequest.username());
        return authenticationService.authenticateUser(authenticationRequest);
    }

}
