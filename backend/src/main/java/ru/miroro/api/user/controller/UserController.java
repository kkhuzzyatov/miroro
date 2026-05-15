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

    // ============================
    // CREATE USER
    // ============================
    @Operation(summary = "Создание пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Пользователь создан"),
        @ApiResponse(responseCode = "409", description = "Username уже занят")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody UserDtoRequest dto) {
        User user = userService.create(dto);
        user.setPasswordHash(null);
        return user;
    }

    // ============================
    // UPDATE USER
    // ============================
    @Operation(summary = "Обновление пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public User update(@PathVariable Integer userId, @RequestBody UserDtoRequest dto, Authentication authentication) {

        if (authentication == null) {
            throw new SessionAuthenticationException("message: Не авторизован");
        }

        User target = userService
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException("message: Пользователь не найден"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = authentication.getName().equals(target.getUsername());

        if (!isAdmin && !isOwner) {
            throw new SecurityException("message: Недостаточно прав");
        }

        User updated = userService.update(userId, dto);
        updated.setPasswordHash(null);

        return updated;
    }

    // ============================
    // GET CURRENT USER
    // ============================
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

    // ============================
    // GET ALL USERS (ADMIN)
    // ============================
    @Operation(summary = "Получение всех пользователей")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список пользователей"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findAll() {

        List<User> users = userService.findAll();

        users.forEach(u -> u.setPasswordHash(null));
        return users;
    }

    // ============================
    // DELETE USER BY USERNAME (ADMIN)
    // ============================
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
        userService.deleteByUsername(username);
    }
}
