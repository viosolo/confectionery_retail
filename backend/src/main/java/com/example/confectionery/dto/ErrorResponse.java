package com.example.confectionery.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String status;
    private String devMessage;
    private int code;
    private LocalDateTime timestamp;
    private String userMessage;
}