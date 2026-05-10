package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> dublicateError(final DublicateException e) {
        log.warn("Ошибка дублирования: {}", e.getMessage());
        return Map.of(
                "error", "Ошибка дублирования",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationError(final ValidateException e) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        return Map.of(
                "error", "Ошибка валидации",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationError(final NoPermutationsException e) {
        log.warn("Ошибка доступа: {}", e.getMessage());
        return Map.of(
                "error", "Ошибка доступа",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> validationError(final NotFoundException e) {
        log.warn("Данные не найдены: {}", e.getMessage());
        return Map.of(
                "error", "Данные не найдены",
                "errorMessage", e.getMessage()
        );
    }

}
