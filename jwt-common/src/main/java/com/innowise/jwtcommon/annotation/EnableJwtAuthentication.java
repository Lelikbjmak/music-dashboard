package com.innowise.jwtcommon.annotation;

import com.innowise.jwtcommon.config.JwtSecurityConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target(value = {ElementType.TYPE})
@Import(value = JwtSecurityConfig.class)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface EnableJwtAuthentication {

}
