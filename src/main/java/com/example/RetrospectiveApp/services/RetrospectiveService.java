package com.example.RetrospectiveApp.services;

import com.example.RetrospectiveApp.exceptions.FeedbackNotFoundException;
import com.example.RetrospectiveApp.exceptions.RetrospectiveExistsException;
import com.example.RetrospectiveApp.exceptions.RetrospectiveNotFoundException;
import com.example.RetrospectiveApp.models.Feedback;
import com.example.RetrospectiveApp.models.Retrospective;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RetrospectiveService {

    private List<Retrospective> retrospectives = new ArrayList<>();

    public Retrospective create(Retrospective retrospective) {
        log.info("Creating retrospective: {}", retrospective.getName());
        for (Retrospective r : retrospectives) {
            if (r.getName().equalsIgnoreCase(retrospective.getName())) {
                log.error("Retrospective with same name already exists: {}", r.getName());
                throw new RetrospectiveExistsException(retrospective.getName());
            }
        }
        retrospective.setFeedback(new ArrayList<>());
        retrospectives.add(retrospective);
        log.info("Retrospective created: {}", retrospective.getName());
        return retrospective;
    }

    private Retrospective findRetrospectiveByName(String requestName) {
        log.debug("Searching for retrospective by name: {}", requestName);
        Retrospective retrospective = retrospectives.stream().filter(c -> c.getName().equals(requestName)).findFirst().orElse(null);
        if (retrospective == null) {
            log.error("Retrospective not found: {}", requestName);
            throw new RetrospectiveNotFoundException(requestName);
        }
        return retrospective;
    }

    public Retrospective addFeedback(String retroName, Feedback feedback) {
        log.info("Adding feedback to retrospective: {}", retroName);
        Retrospective retrospective = findRetrospectiveByName(retroName);
        feedback.setUuid(UUID.randomUUID());
        retrospective.getFeedback().add(feedback);
        log.info("Added feedback to retrospective successfully: {}", retroName);
        return retrospective;
    }

    public Retrospective updateFeedback(String retroName, UUID feedbackId, Feedback newFeedback) {
        log.info("Updating feedback in retrospective: {}", retroName);
        Retrospective retrospective = findRetrospectiveByName(retroName);
        List<Feedback> feedbacks = retrospective.getFeedback();
        Feedback feedback = feedbacks.stream().filter(f -> f.getUuid().equals(feedbackId)).findFirst().orElse(null);
        if (feedback == null) {
            log.error("Feedback not found while trying to update feedback id: {} in retrospective name: {}", feedbackId, retrospective.getName());
            throw new FeedbackNotFoundException(feedbackId);
        }
        feedback.setBody(newFeedback.getBody());
        feedback.setType(newFeedback.getType());
        log.info("Feedback update successfully for retrospective name: {} and feedback id: {}", retrospective.getName(), feedbackId);
        return retrospective;
    }

    public List<Retrospective> getRetrospectives(Integer currentPage, Integer pageSize, LocalDate date) {
        log.info("Fetching retrospective using params currentPage: {}, pageSize: {}, date: {}", currentPage, pageSize, date);
        List<Retrospective> result;
        if (date != null) {
            result = retrospectives.stream().filter(f -> f.getDate().equals(date)).collect(Collectors.toList());
        } else {
            result = retrospectives;
        }

        int fromIndex = (currentPage - 1) * pageSize;
        if (result == null || result.size() <= fromIndex) {
            log.debug("Fetched results is empty or request page index is out of bounds: fromIndex: {}", fromIndex);
            return new ArrayList<>();
        }
        int toIndex = Math.min(fromIndex + pageSize, result.size());
        log.debug("Sending paginated list of retrospectives: fromIndex: {}, toIndex: {}", fromIndex, toIndex);
        return result.subList(fromIndex, toIndex);
    }
}
