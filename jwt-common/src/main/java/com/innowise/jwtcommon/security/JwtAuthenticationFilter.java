package com.innowise.jwtcommon.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.jwtcommon.exception.JwtAuthenticationException;
import com.innowise.jwtcommon.mapper.GrantedAuthorityListMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.innowise.jwtcommon.constant.JwtSecurityConstant.BEARER_TOKEN_HEADER;
import static com.innowise.jwtcommon.constant.JwtSecurityConstant.BEARER_TOKEN_START_INDEX;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GrantedAuthorityListMapper grantedAuthorityListMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.info("JWT filter processing for {}...", request.getRemoteAddr());


        final String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authenticationHeader == null || !authenticationHeader.startsWith(BEARER_TOKEN_HEADER)) {
            log.warn("Bearer token is null for: {}", request.getRemoteAddr());
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authenticationHeader.substring(BEARER_TOKEN_START_INDEX);

        try {
            authenticateUserByJwtToken(jwtToken);
        } catch (JwtAuthenticationException exception) {
            log.warn("JwtAuthenticationException occurred for: {}. Exception: {}", request.getRemoteAddr(), exception.getMessage());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getOutputStream().print(objectMapper.writeValueAsString(exception.getMessage()));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUserByJwtToken(String jwtToken) throws JwtAuthenticationException {
        String username = jwtService.extractUsername(jwtToken);
        List<String> authorities = jwtService.extractRoles(jwtToken);
        List<GrantedAuthority> authoritySet = grantedAuthorityListMapper.mapToEntityList(authorities);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && username == null)
            return;

        if (jwtService.isJwtTokenValid(jwtToken, username)) {
            JwtAuthentication authenticationToken = new JwtAuthentication(jwtToken, username, authoritySet);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
}
