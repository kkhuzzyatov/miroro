package ru.miroro.api.color.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.miroro.api.color.model.Color;
import ru.miroro.api.color.repository.ColorRepository;
import ru.miroro.api.color.service.ColorService;
import ru.miroro.common.security.AuthorizationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/colors")
@Tag(name = "Colors", description = "Управление цветами")
public class ColorController {

    private final ColorRepository repo;
    private final ColorService service;
    private final AuthorizationService authorizationService;

    // ---------- PUBLIC ----------

    @Operation(summary = "Получить все цвета")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список цветов")})
    @GetMapping
    public ResponseEntity<List<Color>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    // ---------- ADMIN ONLY ----------

    @Operation(summary = "Создать цвет")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Цвет создан"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PostMapping
    public ResponseEntity<Color> create(
            @CookieValue(value = "session_token", required = false) String token, @RequestBody Color color) {
        authorizationService.checkAdmin(token);

        Color createdColor = service.create(color);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdColor);
    }

    @Operation(summary = "Обновить цвет")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Цвет обновлён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Цвет не найден"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PutMapping
    public ResponseEntity<Void> update(
            @CookieValue(value = "session_token", required = false) String token,
            @RequestParam("id") int id,
            @RequestBody Color color) {
        authorizationService.checkAdmin(token);

        color.setId(id);
        int updated = repo.update(color);

        if (updated == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить цвет по id")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Цвет удалён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Цвет не найден")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteById(
            @CookieValue(value = "session_token", required = false) String token, @RequestParam("id") int id) {
        authorizationService.checkAdmin(token);

        int deleted = repo.deleteById(id);

        if (deleted == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.noContent().build();
    }
}
