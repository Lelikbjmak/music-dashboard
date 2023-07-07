package com.innowise.authenticationmicroservice.service.impl;

import com.innowise.authenticationmicroservice.dto.AuthenticationRequest;
import com.innowise.authenticationmicroservice.dto.AuthenticationResponse;
import com.innowise.authenticationmicroservice.service.AuthenticationService;
import com.innowise.authenticationmicroservice.util.AuthenticationTokenUtil;
import com.innowise.jwtcommon.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @Override
    public AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest) {

        log.debug("Authenticating user: {}.", authenticationRequest.username());

        UsernamePasswordAuthenticationToken authenticationToken = AuthenticationTokenUtil
                .generate(authenticationRequest.username(), authenticationRequest.password());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        UserDetails loggedUser = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(loggedUser);

        return new AuthenticationResponse(loggedUser.getUsername(), jwtToken);
    }

}
