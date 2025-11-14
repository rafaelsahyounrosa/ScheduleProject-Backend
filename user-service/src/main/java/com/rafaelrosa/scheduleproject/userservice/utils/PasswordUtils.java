package com.rafaelrosa.scheduleproject.userservice.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {

    private PasswordUtils() {}

    public static String encode(String rawPassword) {

        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {

        return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);
    }
}
