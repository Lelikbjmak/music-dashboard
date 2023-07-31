package com.innowise.jwtcommontest.util;


import com.innowise.jwtcommontest.security.TestUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.innowise.jwtcommontest.constant.TestConstant.SIGN_KEY_PRIVATE_METHOD_NAME;

class TestJwtTokenUtilTest {

    @Test
    void mustGenerateToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TestUserDetails testUserDetails = new TestUserDetails(Set.of("ROLE_USER"));
        String generatedToken = TestJwtTokenUtil.generateToken(testUserDetails);

        Method method = TestJwtTokenUtil.class.getDeclaredMethod(SIGN_KEY_PRIVATE_METHOD_NAME);
        method.setAccessible(true);
        Key signKey = (Key) method.invoke(null);


        Assertions.assertNotNull(generatedToken);
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(signKey)
                .build()
                .parseClaimsJws(generatedToken)
                .getBody();

        Assertions.assertEquals(testUserDetails.getUsername(), claims.getSubject());
        Assertions.assertEquals(claims.get("roles", List.class), testUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        Assertions.assertTrue(claims.getExpiration().after(new Date()));
        Assertions.assertTrue(claims.getExpiration().toInstant().toEpochMilli() - new Date().toInstant().toEpochMilli() > 3000);
    }

}