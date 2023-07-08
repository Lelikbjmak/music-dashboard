package com.innowise.usermicroservice.annotation;

import com.innowise.usermicroservice.validation.UsernameConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

import static com.innowise.usermicroservice.constant.ConstraintConstants.USERNAME_REGEX_PATTERN;

@Documented
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@NotBlank(message = "Username is mandatory.")
@Constraint(validatedBy = UsernameConstraintValidator.class)
@Pattern(regexp = USERNAME_REGEX_PATTERN, message = "Not valid format.")
public @interface ValidUsername {

    String message() default "Invalid username.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
