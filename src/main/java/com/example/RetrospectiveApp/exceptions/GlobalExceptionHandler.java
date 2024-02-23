package com.example.RetrospectiveApp.exceptions;


import com.example.RetrospectiveApp.dtos.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RetrospectiveExistsException.class)
    public ResponseEntity<ErrorResponse> handleRetrospectiveExistsException(RetrospectiveExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(HttpStatus.CONFLICT.value(), 1, Collections.singletonList(e.getMessage())));
    }

    @ExceptionHandler(RetrospectiveNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRetrospectiveNotFoundException(RetrospectiveNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 1, Collections.singletonList(e.getMessage())));
    }

    @ExceptionHandler(FeedbackNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRetrospectiveExistsException(FeedbackNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 1, Collections.singletonList(e.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        ArrayList<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + ": " + errorMessage);
        });
        log.error("Bad request from api {} : {}", req.getRequestURI(), errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 100, errors));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableExceptions(
            Exception ex) {
        log.error("GlobalExceptionHandler caught an exception: {} {}", ex.getMessage(), ex.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 1, Collections.singletonList(ex.getMessage())));
    }
}
