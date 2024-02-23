package com.example.RetrospectiveApp.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    // Added id as cannot assume personName to be unique. The id will also be used to update feedback
    // It makes sense for same person to be able to provide multiple feedbacks per retro
    private UUID uuid;
    private String personName;
    @NotBlank(message = "Feedback body is required")
    private String body;
    @NotNull(message = "Feedback type is required (POSITIVE/NEGATIVE/IDEA/PRAISE)")
    private FeedbackType type;
}
