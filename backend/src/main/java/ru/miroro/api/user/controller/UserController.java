package ru.miroro.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.bind.annotation.*;
import ru.miroro.api.user.dto.UserDtoRequest;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Управление пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Создание пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Пользователь создан"),
        @ApiResponse(responseCode = "409", description = "Username уже занят")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody UserDtoRequest dto) {

        log.atInfo()
                .addKeyValue("endpoint", "POST /api/users")
                .addKeyValue("username", dto.getUsername())
                .log("Создание пользователя");

        User user = userService.create(dto);
        user.setPasswordHash(null);
        return user;
    }

    @Operation(summary = "Обновление пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public User update(@PathVariable Integer id, @RequestBody UserDtoRequest dto, Authentication authentication) {

        log.atInfo()
                .addKeyValue("endpoint", "PUT /api/users/{id}")
                .addKeyValue("userId", id)
                .addKeyValue("authUser", authentication != null ? authentication.getName() : null)
                .log("Обновление пользователя");

        if (authentication == null) {
            throw new SessionAuthenticationException("message: Не авторизован");
        }

        User target = userService
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("message: Пользователь не найден"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = authentication.getName().equals(target.getUsername());

        if (!isAdmin && !isOwner) {
            throw new SecurityException("message: Недостаточно прав");
        }

        User updated = userService.update(id, dto);
        updated.setPasswordHash(null);

        return updated;
    }

    @Operation(summary = "Получение текущего пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь найден"),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/me")
    public User me(Authentication authentication) {

        log.atInfo()
                .addKeyValue("endpoint", "GET /api/users/me")
                .addKeyValue("authUser", authentication != null ? authentication.getName() : null)
                .log("Получение текущего пользователя");

        if (authentication == null) {
            throw new SessionAuthenticationException("message: Не авторизован");
        }

        User user = userService
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new SecurityException("message: Не авторизован"));

        user.setPasswordHash(null);
        return user;
    }

    @Operation(summary = "Получение всех пользователей")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список пользователей"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findAll() {

        log.atInfo()
                .addKeyValue("endpoint", "GET /api/users")
                .addKeyValue("role", "ADMIN")
                .log("Получение всех пользователей");

        List<User> users = userService.findAll();
        users.forEach(u -> u.setPasswordHash(null));
        return users;
    }

    @Operation(summary = "Удаление пользователя по username")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByUsername(@PathVariable String username) {

        log.atInfo()
                .addKeyValue("endpoint", "DELETE /api/users/{username}")
                .addKeyValue("username", username)
                .log("Удаление пользователя");

        userService.deleteByUsername(username);
    }
}
