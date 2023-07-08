package com.innowise.usermicroservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

import static com.innowise.usermicroservice.constant.ConstraintConstants.PASSWORD_REGEX_PATTERN;

@Documented
@Constraint(validatedBy = {})
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Pattern(regexp = PASSWORD_REGEX_PATTERN, message = "Password must contain at least 8 and no more than 25 chars, at least 1 digit and 1 UpperCase char is compulsory.")
public @interface ValidPassword {

    String message() default "Password is not valid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
