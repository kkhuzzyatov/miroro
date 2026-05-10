package ru.miroro.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.miroro.api.session.model.Session;
import ru.miroro.api.session.service.SessionService;
import ru.miroro.api.user.dto.UserDtoRequest;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Управление пользователями")
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;

    @Operation(summary = "Создание пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Пользователь создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "404", description = "Недостижимый username")
    })
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody UserDtoRequest dto) {
        userService.create(dto);
    }

    @Operation(summary = "Обновление пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
        @ApiResponse(responseCode = "404", description = "Пользователя с таким username нет")
    })
    @PutMapping("")
    public User update(
            @RequestParam("id") Long userId,
            @RequestBody UserDtoRequest dto,
            @CookieValue(value = "session_token", required = false) String token) {

        Session session = null;
        if (token != null) {
            session =
                    sessionService.getSessionByToken(token).orElseThrow(() -> new SecurityException("Не авторизован"));
        }

        if (session != null && !"admin".equals(session.getRole())) {
            User user = userService
                    .findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            if (!session.getUsername().equals(user.getUsername())) {
                throw new SecurityException("Недостаточно прав");
            }
        }

        User user = userService.update(userId, dto);
        user.setPasswordHash(null);
        return user;
    }

    @Operation(summary = "Получение текущего пользователя по сессии")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь найден"),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("")
    public User findByCookie(@CookieValue(value = "session_token", required = false) String token) {

        Session session =
                sessionService.getSessionByToken(token).orElseThrow(() -> new SecurityException("Не авторизован"));

        return userService
                .findByUsername(session.getUsername())
                .orElseThrow(() -> new SecurityException("Не авторизован"));
    }

    @Operation(summary = "Получение всех пользователей (admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список пользователей"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping("/all")
    public List<User> findAll(@CookieValue(value = "session_token", required = false) String token) {

        Session session =
                sessionService.getSessionByToken(token).orElseThrow(() -> new SecurityException("Не авторизован"));

        if (!"admin".equals(session.getRole())) {
            throw new SecurityException("Доступ запрещён");
        }

        return userService.findAll();
    }

    @Operation(summary = "Удаление пользователя по username (admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Пользователя с таким username нет")
    })
    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByUsername(
            @RequestParam("username") String username,
            @CookieValue(value = "session_token", required = false) String token) {

        Session session =
                sessionService.getSessionByToken(token).orElseThrow(() -> new SecurityException("Не авторизован"));

        if (!"admin".equals(session.getRole())) {
            throw new SecurityException("Доступ запрещён");
        }

        userService.deleteByUsername(username);
    }
}
