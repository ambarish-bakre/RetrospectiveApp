package com.example.RetrospectiveApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private int code;
    private int extendedErrorCode;
    private List<String> errors;
}
