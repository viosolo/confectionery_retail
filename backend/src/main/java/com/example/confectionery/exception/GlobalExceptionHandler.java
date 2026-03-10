package com.example.confectionery.exception;

import com.example.confectionery.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(UserAlreadyExistsException e) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "Ошибка регистрации",
                e
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ResourceNotFoundException e) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Данные не найдены",
                e
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка сервера",
                e
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, Throwable throwable) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.getReasonPhrase(),
                message,
                throwable.getMessage()
        );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}