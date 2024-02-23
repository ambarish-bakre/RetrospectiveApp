package com.example.RetrospectiveApp.exceptions;

public class RetrospectiveNotFoundException extends RuntimeException {
    public RetrospectiveNotFoundException(String retrospectiveName) {
        super("Retrospective not found: " + retrospectiveName);
    }
}
