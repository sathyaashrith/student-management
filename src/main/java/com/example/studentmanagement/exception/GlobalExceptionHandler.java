package com.example.studentmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 - Student Not Found
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFound(
            StudentNotFoundException exception) {
        log.warn(
                "Student not found: {}",
                exception.getMessage()
        );
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.NOT_FOUND
        );
    }

    // 409 - Duplicate Student
    @ExceptionHandler(StudentDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateStudent(
            StudentDuplicateException exception) {

        log.warn(
                "Duplicate student request: {}",
                exception.getMessage()
        );
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.CONFLICT
        );
    }

    // 400 - Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception) {

        log.warn(
                "Illegal argument: {}",
                exception.getMessage()
        );
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.BAD_REQUEST
        );
    }

    // 500 - Unexpected Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception exception) {
        log.error(
                "Unexpected application error",
                exception
        );
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage())
                .collect(java.util.stream.Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.BAD_REQUEST
        );
    }
}