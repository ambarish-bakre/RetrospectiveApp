package com.example.RetrospectiveApp.exceptions;

public class RetrospectiveExistsException extends RuntimeException {
    public RetrospectiveExistsException(String existingRetrospective) {
        super("Retrospective already exists: " + existingRetrospective);
    }
}
