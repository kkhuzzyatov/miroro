package ru.miroro.common.exception_handler;

import jakarta.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "ru.miroro.api")
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, "Некорректный запрос");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        return buildResponse(ex, HttpStatus.UNAUTHORIZED, "Не авторизован");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponse(ex, HttpStatus.FORBIDDEN, "Доступ запрещён");
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException ex) {
        return buildResponse(ex, HttpStatus.FORBIDDEN, "Ошибка безопасности: доступ запрещён");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElementException(NoSuchElementException ex) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, "Запрашиваемый элемент не найден");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, "Сущность не найдена");
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Map<String, String>> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, "Данные не найдены");
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, String>> handleSqlException(SQLException ex) {
        return buildResponse(ex, HttpStatus.CONFLICT, "Ошибка базы данных. Попробуйте позже");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateKeyException(DuplicateKeyException ex) {
        return buildResponse(ex, HttpStatus.CONFLICT, "Конфликт данных: запись уже существует");
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера. Попробуйте ещё раз");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Непредвиденная ошибка. Попробуйте позже");
    }

    private ResponseEntity<Map<String, String>> buildResponse(Exception ex, HttpStatus status, String defaultMessage) {
        String rawMessage = ex.getMessage();

        String message;
        if (rawMessage != null && rawMessage.startsWith("message: ")) {
            message = rawMessage.replace("message: ", "");
        } else {
            message = defaultMessage;
        }

        return ResponseEntity.status(status).body(Map.of("message", message));
    }
}
