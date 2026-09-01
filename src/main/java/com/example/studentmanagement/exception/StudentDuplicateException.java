package com.example.studentmanagement.exception;

public class StudentDuplicateException extends RuntimeException {

    public StudentDuplicateException(String message) {
        super(message);
    }
}