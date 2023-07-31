package com.innowise.jwtcommon.security;

import com.innowise.jwtcommon.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private final long tokenValidity;

    private final String tokenSecretKey;

    public String generateToken(UserDetails userDetails) {
        log.debug("Generating JwtToken for UserDetails: {}", userDetails.getUsername());
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))   // 2 hours
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isJwtTokenValid(String jwtToken, String username) throws JwtAuthenticationException {
        log.debug("Validating JwtToken: {} for UserDetails: {}", jwtToken, username);
        final String tokenUsername = extractUsername(jwtToken);
        return tokenUsername.equals(username) && !isTokenExpired(jwtToken);
    }

    public String extractUsername(String jwtToken) throws JwtAuthenticationException {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public List<String> extractRoles(String jwtToken) throws JwtAuthenticationException {
        Claims claims = extractAllClaims(jwtToken);
        return claims.get("roles", List.class);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String jwtToken) throws JwtAuthenticationException {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
        } catch (ExpiredJwtException exception) {
            throw new JwtAuthenticationException("Session expired.");
        } catch (UnsupportedJwtException | SignatureException exception) {
            throw new JwtAuthenticationException("Invalid session.");
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtAuthenticationException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date extractExpiration(String jwtToken) throws JwtAuthenticationException {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    public boolean isTokenExpired(String jwtToken) throws JwtAuthenticationException {
        return extractExpiration(jwtToken).before(new Date());
    }

}
