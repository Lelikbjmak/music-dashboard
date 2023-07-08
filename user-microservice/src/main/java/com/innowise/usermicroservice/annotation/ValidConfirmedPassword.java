package com.innowise.usermicroservice.annotation;

import com.innowise.usermicroservice.validation.ConfirmedPasswordConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConfirmedPasswordConstraintValidator.class)
public @interface ValidConfirmedPassword {

    String message() default "Confirmed password doesn't match.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
