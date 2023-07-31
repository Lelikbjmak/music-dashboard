package com.innowise.jwtcommon.annotation;

import com.innowise.jwtcommon.config.PasswordEncoderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Import(value = PasswordEncoderConfig.class)
public @interface EnablePasswordEncoder {

}
