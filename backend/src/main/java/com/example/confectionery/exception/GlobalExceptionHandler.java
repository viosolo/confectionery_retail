package com.example.confectionery.exception;

import com.example.confectionery.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(UserAlreadyExistsException e) {
        log.error("Конфликт при регистрации: {}", e.getMessage());
        return buildResponse(
                HttpStatus.CONFLICT,
                "Пользователь с такими данными уже существует",
                e.getMessage()
        );
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(AlreadyExistsException e) {

        log.error(">>> Conflict (Resource already exists): {}", e.getMessage());
        return buildResponse(
                HttpStatus.CONFLICT,
                "Данный ресурс уже существует",
                e.getMessage()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ResourceNotFoundException e) {
        log.error("Ресурс не найден: {}", e.getMessage());
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Извините, запрашиваемая информация не найдена",
                e.getMessage()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException e) {
        log.error("Некорректный запрос: {}", e.getMessage());
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Не удалось выполнить запрос. Проверьте введенные данные",
                e.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {

        String details = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.error("Ошибка валидации данных: {}", details);

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации данных",
                details
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {

        log.error("Критическая ошибка сервера: ", e);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка сервера. Мы уже работаем над исправлением!",
                e.getClass().getSimpleName() + ": " + e.getMessage()
        );
    }


    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String userMsg, String devMsg) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.getReasonPhrase(),
                devMsg,
                status.value(),
                LocalDateTime.now(),
                userMsg
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
}