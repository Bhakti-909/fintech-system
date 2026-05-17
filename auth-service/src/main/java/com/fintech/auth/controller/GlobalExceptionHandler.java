package com.fintech.auth.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// INTERVIEW: "@ControllerAdvice intercepts exceptions thrown from any controller
// and returns a proper JSON error response instead of a stack trace.
// This is the correct way to handle errors globally in Spring Boot."
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Something went wrong"));
    }
}
