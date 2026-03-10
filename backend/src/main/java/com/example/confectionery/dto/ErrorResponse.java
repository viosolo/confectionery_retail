package com.example.confectionery.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String status;      // Например, "404 NOT_FOUND"
    private String devMessage;  // Сообщение из Exception (e.getMessage())
    private String userMessage; // Понятное сообщение (например, "Данные не найдены")
}