package ru.miroro.api.session.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.miroro.api.session.dto.LoginRequest;
import ru.miroro.api.session.model.SessionData;
import ru.miroro.api.session.service.SessionService;
import ru.miroro.api.session.service.SessionStorageService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Sessions", description = "Управление сессиями пользователей")
public class SessionController {

    private final SessionService sessionService;
    private final SessionStorageService sessionStorageService;

    @Operation(summary = "Вход пользователя (логин)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь успешно вошёл в систему"),
        @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request, HttpServletResponse response) {

        String token = sessionService.login(request.username(), request.password());

        SessionData session = sessionStorageService.get(token);

        if (session == null) {
            throw new IllegalStateException("Session not found in Redis");
        }

        Cookie cookie = new Cookie("session_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);

        log.atInfo()
                .addKeyValue("endpoint", "POST /api/sessions/login")
                .addKeyValue("username", session.getUsername())
                .addKeyValue("role", session.getRole())
                .log("Создана сессия");

        return Map.of(
                "username", session.getUsername(),
                "role", session.getRole());
    }

    @Operation(summary = "Выход пользователя (logout)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Пользователь успешно вышел из системы"),
        @ApiResponse(responseCode = "403", description = "Не авторизован")
    })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            @CookieValue(value = "session_token", required = false) String token, HttpServletResponse response) {

        log.atInfo()
                .addKeyValue("endpoint", "POST /api/sessions/logout")
                .addKeyValue("hasToken", token != null && !token.isBlank())
                .log("Выход из системы");

        if (token == null || token.isBlank()) {
            throw new SecurityException("message: Не авторизован");
        }

        Cookie cookie = new Cookie("session_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        sessionStorageService.delete(token);
    }

    @Operation(summary = "Проверка авторизации")
    @ApiResponse(responseCode = "200", description = "Статус авторизации")
    @GetMapping("/me")
    public Map<String, Object> me(@CookieValue(value = "session_token", required = false) String token) {

        boolean authenticated = token != null && !token.isBlank() && sessionStorageService.exists(token);

        log.atInfo()
                .addKeyValue("endpoint", "GET /api/sessions/me")
                .addKeyValue("authenticated", authenticated)
                .log("Проверка сессии");

        return Map.of("authenticated", authenticated);
    }
}
