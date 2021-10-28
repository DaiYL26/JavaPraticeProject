package com.example.login.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String getEncodePwd(CharSequence pwd) {
        return passwordEncoder.encode(pwd);
    }

    public static boolean match(CharSequence rawPwd, String encode) {
        return passwordEncoder.matches(rawPwd, encode);
    }

}
