package ru.miroro.api.session.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.miroro.api.session.dto.LoginRequest;
import ru.miroro.api.session.model.Session;
import ru.miroro.api.session.service.SessionService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Sessions", description = "Управление сессиями пользователей")
public class SessionController {

    private final SessionService sessionService;

    @Operation(summary = "Вход пользователя (логин)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь успешно вошёл в систему"),
        @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    @PostMapping("/login")
    public Session login(@RequestBody LoginRequest request, HttpServletResponse response) {

        Session session = sessionService.login(request.username(), request.password());

        Cookie cookie = new Cookie("session_token", session.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 дней
        response.addCookie(cookie);

        System.out.println("new_session: " + session.getUsername());
        return session;
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
        if (token == null || token.isBlank()) {
            throw new SecurityException("message: Не авторизован");
        }

        Cookie cookie = new Cookie("session_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        sessionService.logout(token);
    }

    @Operation(summary = "Проверка авторизации")
    @ApiResponse(responseCode = "200", description = "Статус авторизации")
    @GetMapping("/me")
    public Map<String, Object> me(@CookieValue(value = "session_token", required = false) String token) {

        boolean authenticated = false;

        if (token != null && !token.isBlank()) {
            authenticated = sessionService.isValid(token);
        }

        return Map.of("authenticated", authenticated);
    }
}
