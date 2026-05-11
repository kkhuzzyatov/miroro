package ru.miroro.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.bind.annotation.*;
import ru.miroro.api.user.dto.UserDtoRequest;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Управление пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Создание пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Пользователь создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "409", description = "Username уже занят")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody UserDtoRequest dto) {
        userService.create(dto);
    }

    @Operation(summary = "Обновление пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping
    public User update(
            @RequestParam("id") Long userId, @RequestBody UserDtoRequest dto, Authentication authentication) {
        if (authentication == null) {
            throw new SessionAuthenticationException("message: Не авторизован");
        }

        User targetUser = userService
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException("message: Пользователь не найден"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = authentication.getName().equals(targetUser.getUsername());

        if (!isAdmin && !isOwner) {
            throw new SecurityException("message: Недостаточно прав");
        }

        User updatedUser = userService.update(userId, dto);
        updatedUser.setPasswordHash(null);

        return updatedUser;
    }

    @Operation(summary = "Получение текущего пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь найден"),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/me")
    public User me(Authentication authentication) {
        if (authentication == null) {
            throw new SessionAuthenticationException("message: Не авторизован");
        }

        User user = userService
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new SecurityException("message: Не авторизован"));

        user.setPasswordHash(null);

        return user;
    }

    @Operation(summary = "Получение всех пользователей (admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список пользователей"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findAll() {

        List<User> users = userService.findAll();

        users.forEach(user -> user.setPasswordHash(null));

        return users;
    }

    @Operation(summary = "Удаление пользователя по username (admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteByUsername(@RequestParam("username") String username) {
        userService.deleteByUsername(username);
    }
}
