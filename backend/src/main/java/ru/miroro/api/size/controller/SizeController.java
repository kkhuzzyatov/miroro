package ru.miroro.api.size.controller;

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
import ru.miroro.api.size.model.Size;
import ru.miroro.api.size.service.SizeService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sizes")
@Tag(name = "Sizes", description = "Управление размерами")
public class SizeController {

    private final SizeService sizeService;

    @Operation(summary = "Получить все размеры")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список размеров")})
    @GetMapping
    public ResponseEntity<List<Size>> getAll() {

        log.atInfo().addKeyValue("endpoint", "GET /api/sizes").log("Получение всех размеров");

        return ResponseEntity.ok(sizeService.findAll());
    }

    @Operation(summary = "Получить размер по id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Размер найден"),
        @ApiResponse(responseCode = "404", description = "Размер не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Size> getById(@PathVariable int id) {

        log.atInfo()
                .addKeyValue("endpoint", "GET /api/sizes/{id}")
                .addKeyValue("sizeId", id)
                .log("Получение размера по id");

        Size size = sizeService.findById(id);

        if (size == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(size);
    }

    @Operation(summary = "Создать размер")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Размер создан"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Size> create(@RequestBody Size size) {

        log.atInfo()
                .addKeyValue("endpoint", "POST /api/sizes")
                .addKeyValue("sizeName", size.getName())
                .log("Создание размера");

        Size created = sizeService.create(size);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Обновить размер")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Размер обновлён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Размер не найден"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> update(@PathVariable int id, @RequestBody Size size) {

        log.atInfo()
                .addKeyValue("endpoint", "PUT /api/sizes/{id}")
                .addKeyValue("sizeId", id)
                .addKeyValue("sizeName", size.getName())
                .log("Обновление размера");

        boolean updated = sizeService.update(id, size);

        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить размер")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Размер удалён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Размер не найден"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable int id) {

        log.atInfo()
                .addKeyValue("endpoint", "DELETE /api/sizes/{id}")
                .addKeyValue("sizeId", id)
                .log("Удаление размера");

        boolean deleted = sizeService.deleteById(id);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.noContent().build();
    }
}
