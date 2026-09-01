package com.example.studentmanagement;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String password = "admin123";;

        String encodedPassword =
                encoder.encode(password);

        System.out.println(encodedPassword);
    }
}