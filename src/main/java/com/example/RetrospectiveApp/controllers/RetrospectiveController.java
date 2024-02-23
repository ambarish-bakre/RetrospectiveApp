package com.example.RetrospectiveApp.controllers;

import com.example.RetrospectiveApp.models.Feedback;
import com.example.RetrospectiveApp.models.Retrospective;
import com.example.RetrospectiveApp.services.RetrospectiveService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/retrospectives")
public class RetrospectiveController {

    @Autowired
    private RetrospectiveService retrospectiveService;

    // Create new retrospective
    @PostMapping
    public ResponseEntity<Retrospective> createRetrospective(@Valid @RequestBody Retrospective retrospective) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(retrospectiveService.create(retrospective));
    }

    // List/Search all retrospectives
    // To produce JSON or XML response, Spring prefers to use the 'Accept' header instead of 'Content-Type' header
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Retrospective>> getRetrospectives(@Min(value = 1, message = "Current page cannot be less than 1") @RequestParam(defaultValue = "1") Integer currentPage,
                                                                 @Min(value = 1, message = "Page size cannot be less than 1") @RequestParam(defaultValue = "10") Integer pageSize,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/MM/yyyy") LocalDate date) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(retrospectiveService.getRetrospectives(currentPage, pageSize, date));
    }

    // Add feedback to retrospective
    @PostMapping("{retroName}/feedback")
    public ResponseEntity<Retrospective> addFeedback(@PathVariable("retroName") String requestName,
                                                     @Valid @RequestBody Feedback feedback) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(retrospectiveService.addFeedback(requestName, feedback));
    }

    // Update feedback of retrospective
    @PutMapping("{retroName}/feedback/{feedbackId}")
    public ResponseEntity<Retrospective> updateFeedback(@PathVariable("retroName") String requestName,
                                                        @PathVariable("feedbackId") UUID feedbackId,
                                                        @Valid @RequestBody Feedback newFeedback) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(retrospectiveService.updateFeedback(requestName, feedbackId, newFeedback));
    }
}
