package com.innowise.usermicroservice.annotation;

import com.innowise.usermicroservice.validation.EmailConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.lang.annotation.*;

import static com.innowise.usermicroservice.util.ApplicationConstant.EMAIL_REGEX_PATTERN;

@Documented
@Target(value = {ElementType.FIELD})
@NotBlank(message = "Email is mandatory.")
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailConstraintValidator.class)
@Email(regexp = EMAIL_REGEX_PATTERN, message = "Not valid format.")
public @interface ValidEmail {

    String message() default "Invalid email.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
