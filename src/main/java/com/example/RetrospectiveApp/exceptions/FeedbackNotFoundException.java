package com.example.RetrospectiveApp.exceptions;

import java.util.UUID;

public class FeedbackNotFoundException extends RuntimeException {
    public FeedbackNotFoundException(UUID existingRetrospective) {
        super("Feedback not found exception: " + existingRetrospective);
    }
}
