package com.innowise.usermicroservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstraintConstants {

    public static final String USERNAME_REGEX_PATTERN = "^\\w{5,25}$";
    public static final String PASSWORD_REGEX_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,25}$";
    public static final String EMAIL_REGEX_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

}
