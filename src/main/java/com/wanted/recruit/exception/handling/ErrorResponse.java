package com.wanted.recruit.exception.handling;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private String title;
    private String message;
}
