package com.innowise.authenticationmicroservice.service;


import com.innowise.authenticationmicroservice.dto.AuthenticationRequest;
import com.innowise.authenticationmicroservice.dto.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest);

}
