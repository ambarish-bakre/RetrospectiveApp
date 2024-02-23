package com.example.RetrospectiveApp.services;

import com.example.RetrospectiveApp.exceptions.FeedbackNotFoundException;
import com.example.RetrospectiveApp.exceptions.RetrospectiveExistsException;
import com.example.RetrospectiveApp.exceptions.RetrospectiveNotFoundException;
import com.example.RetrospectiveApp.models.Feedback;
import com.example.RetrospectiveApp.models.FeedbackType;
import com.example.RetrospectiveApp.models.Retrospective;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RetrospectiveServiceTest {
    private RetrospectiveService retrospectiveService;

    @BeforeEach
    void setUp() {
        retrospectiveService = new RetrospectiveService();
    }

    @AfterEach
    void tearDown() {
        retrospectiveService = null;
    }

    @Test
    void createShouldCreateRetrospective() {
        Retrospective retrospective = new Retrospective(
                "Retrospective 1",
                "Release end retrospective",
                LocalDate.of(2023, 11, 29),
                Arrays.asList("Superman", "Batman"), List.of()
        );

        Retrospective createdRetrospective = retrospectiveService.create(retrospective);

        Assertions.assertEquals(retrospective.getName(), createdRetrospective.getName());
        Assertions.assertEquals(retrospective.getSummary(), createdRetrospective.getSummary());
        Assertions.assertEquals(retrospective.getDate(), createdRetrospective.getDate());
        Assertions.assertEquals(retrospective.getParticipants(), createdRetrospective.getParticipants());
        Assertions.assertEquals(1, retrospectiveService.retrospectives.size());
    }

    @Test
    void createShouldThrowExceptionWhenRetrospectiveAlreadyExists() {
        Retrospective retrospective = new Retrospective(
                "Retrospective 1",
                "Release end retrospective",
                LocalDate.of(2023, 11, 29),
                Arrays.asList("Superman", "Batman"), List.of()
        );

        retrospectiveService.create(retrospective);
        RetrospectiveExistsException ex = Assertions.assertThrowsExactly(
                RetrospectiveExistsException.class,
                () -> {
                    retrospectiveService.create(retrospective);
                });

        Assertions.assertEquals("Retrospective already exists: Retrospective 1", ex.getMessage());
        Assertions.assertEquals(1, retrospectiveService.retrospectives.size());
    }

    @Test
    void addFeedbackShouldAddFeedbackToRetrospective() {
        Retrospective retrospective = new Retrospective("Retrospective 1",
                "Release end retrospective",
                LocalDate.of(2023, 11, 29),
                Arrays.asList("Superman", "Batman"), List.of());
        retrospectiveService.create(retrospective);
        retrospectiveService.addFeedback("Retrospective 1",
                new Feedback(null, "Superman", "Good Work!", FeedbackType.POSITIVE));
        List<Feedback> feedbacks = retrospectiveService.retrospectives.get(0).getFeedback();

        Assertions.assertEquals(1, feedbacks.size());
        Assertions.assertNotNull(feedbacks.get(0));
        Assertions.assertNotNull(feedbacks.get(0).getUuid());
    }

    @Test
    void addFeedbackShouldThrowIfRetrospectiveNameNotFound() {
        Retrospective retrospective = new Retrospective("Retrospective 1",
                "Release end retrospective",
                LocalDate.of(2023, 11, 29),
                Arrays.asList("Superman", "Batman"), List.of());
        retrospectiveService.create(retrospective);

        RetrospectiveNotFoundException ex = Assertions.assertThrowsExactly(RetrospectiveNotFoundException.class, () -> {
            retrospectiveService.addFeedback("Retrospective 2",
                    new Feedback(null, "Superman", "Good Work!", FeedbackType.POSITIVE));
        });
        Assertions.assertEquals("Retrospective not found: Retrospective 2", ex.getMessage());
    }

    @Test
    void updateFeedbackShouldUpdateFeedback() {
        Retrospective retrospective = new Retrospective("Retrospective 1",
                "Release end retrospective",
                LocalDate.of(2023, 11, 29),
                Arrays.asList("Superman", "Batman"), List.of());
        retrospectiveService.create(retrospective);
        retrospectiveService.addFeedback("Retrospective 1",
                new Feedback(null, "Superman", "Good Work!", FeedbackType.POSITIVE));
        Feedback feedback = retrospectiveService.retrospectives.get(0).getFeedback().get(0);

        retrospectiveService.updateFeedback(
                "Retrospective 1",
                feedback.getUuid(),
                new Feedback(null, null, "Not Good", FeedbackType.NEGATIVE)
        );

        Feedback updatedFeedback = retrospectiveService.retrospectives.get(0).getFeedback().get(0);

        Assertions.assertEquals("Not Good", updatedFeedback.getBody());
        Assertions.assertEquals(FeedbackType.NEGATIVE, updatedFeedback.getType());
    }

    @Test
    void updateFeedbackShouldThrowErrorIfFeedbackIdIsNotFound() {
        Retrospective retrospective = new Retrospective("Retrospective 1",
                "Release end retrospective",
                LocalDate.of(2023, 11, 29),
                Arrays.asList("Superman", "Batman"), List.of());
        retrospectiveService.create(retrospective);
        UUID id = UUID.randomUUID();
        FeedbackNotFoundException ex = Assertions.assertThrowsExactly(FeedbackNotFoundException.class, () -> {
            retrospectiveService.updateFeedback(
                    "Retrospective 1",
                    id,
                    new Feedback(null, null, "Not Good", FeedbackType.NEGATIVE)
            );
        });

        Assertions.assertEquals("Feedback not found exception: " + id, ex.getMessage());

    }

    @Test
    void getRetrospectivesShouldPaginateRetrospectives() {
        Retrospective retrospective1 = new Retrospective(
                "Retrospective 1",
                "Release end retrospective",
                LocalDate.of(2023, 11, 29),
                Arrays.asList("Superman", "Batman"), List.of()
        );
        Retrospective retrospective2 = new Retrospective(
                "Retrospective 2",
                "Release end retrospective",
                LocalDate.of(2023, 11, 29),
                Arrays.asList("Superman", "Batman"), List.of()
        );
        retrospectiveService.create(retrospective1);
        retrospectiveService.create(retrospective2);

        List<Retrospective> retrospectives = retrospectiveService.getRetrospectives(1, 1, null);

        Assertions.assertEquals(1, retrospectives.size());
        Assertions.assertNotNull(retrospectives.get(0));
        Assertions.assertEquals(retrospective1.getName(), retrospectives.get(0).getName());
    }

    @Test
    void getRetrospectivesShouldSendEmptyListForPageOutsideBounds() {
        Retrospective retrospective1 = new Retrospective(
                "Retrospective 1",
                "Release end retrospective",
                LocalDate.of(2023, 11, 29),
                Arrays.asList("Superman", "Batman"), List.of()
        );
        Retrospective retrospective2 = new Retrospective(
                "Retrospective 2",
                "Release end retrospective",
                LocalDate.of(2023, 11, 29),
                Arrays.asList("Superman", "Batman"), List.of()
        );
        retrospectiveService.create(retrospective1);
        retrospectiveService.create(retrospective2);

        List<Retrospective> retrospectives = retrospectiveService.getRetrospectives(3, 1, null);

        Assertions.assertEquals(0, retrospectives.size());
    }

    @Test
    void getRetrospectivesShouldFilterByDateIfPassed() {
        Retrospective retrospective1 = new Retrospective(
                "Retrospective 1",
                "Release end retrospective",
                LocalDate.of(2023, 11, 29),
                Arrays.asList("Superman", "Batman"), List.of()
        );
        Retrospective retrospective2 = new Retrospective(
                "Retrospective 2",
                "Release end retrospective",
                LocalDate.of(2023, 11, 30),
                Arrays.asList("Superman", "Batman"), List.of()
        );
        retrospectiveService.create(retrospective1);
        retrospectiveService.create(retrospective2);

        List<Retrospective> retrospectives = retrospectiveService.getRetrospectives(1, 1, LocalDate.of(2023, 11, 30));

        Assertions.assertEquals(1, retrospectives.size());
        Assertions.assertNotNull(retrospectives.get(0));
        Assertions.assertEquals(retrospective2.getName(), retrospectives.get(0).getName());
    }

}