package com.innowise.jwtcommontest.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;

@Slf4j
@UtilityClass
public class TestJwtTokenUtil {

    private static final long TOKEN_VALIDITY = 5_000L;

    private static final String TOKEN_SECRET_KEY = "2B4D6251655468576D5A7134743777217A25432A462D4A404E635266556A586E";

    public static String generateToken(UserDetails userDetails) {
        log.debug("Generating TestJwtToken for UserDetails: {}", userDetails.getUsername());
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(TOKEN_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
