package ru.miroro.api.color.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.miroro.api.color.model.Color;
import ru.miroro.api.color.repository.ColorRepository;
import ru.miroro.api.color.service.ColorService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/colors")
@Tag(name = "Colors", description = "Управление цветами")
public class ColorController {

    private final ColorRepository repo;
    private final ColorService service;

    // ---------- PUBLIC ----------

    @Operation(summary = "Получить все цвета")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список цветов")})
    @GetMapping
    public ResponseEntity<List<Color>> getAll() {

        log.atInfo().addKeyValue("endpoint", "GET /api/colors").log("Получение списка цветов");

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Color> create(@RequestBody Color color) {

        log.atInfo()
                .addKeyValue("endpoint", "POST /api/colors")
                .addKeyValue("colorName", color.getName())
                .log("Создание цвета");

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
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Color> update(@PathVariable int id, @RequestBody Color color) {

        log.atInfo()
                .addKeyValue("endpoint", "PUT /api/colors/{id}")
                .addKeyValue("colorId", id)
                .addKeyValue("colorName", color.getName())
                .log("Обновление цвета");

        Color updatedColor = service.update(id, color);

        return ResponseEntity.ok(updatedColor);
    }

    @Operation(summary = "Удалить цвет по id")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Цвет удалён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Цвет не найден")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {

        log.atInfo()
                .addKeyValue("endpoint", "DELETE /api/colors/{id}")
                .addKeyValue("colorId", id)
                .log("Удаление цвета");

        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
