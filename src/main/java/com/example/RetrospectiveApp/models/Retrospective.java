package com.example.RetrospectiveApp.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class Retrospective {
    @NotBlank(message = "Retrospective name is required")
    private String name;

    @NotNull(message = "Retrospective summary is required")
    private String summary;

    // TODO write a custom validator to validate dates
    @NotNull(message = "Retrospective date is required. Date format must be dd/MM/yyyy")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    @NotEmpty(message = "Retrospective must have at least 1 participant")
    private List<@NotBlank String> participants;

    private List<Feedback> feedback;
}
