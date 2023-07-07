package com.innowise.authenticationmicroservice.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@UtilityClass
public class AuthenticationTokenUtil {

    public static UsernamePasswordAuthenticationToken generate(String username, String password) {
        return new UsernamePasswordAuthenticationToken(
                username, password
        );
    }
}
