package ru.miroro.api.size.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.miroro.api.size.model.Size;
import ru.miroro.api.size.repository.SizeRepository;
import ru.miroro.api.size.service.SizeService;
import ru.miroro.common.security.AuthorizationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sizes")
@Tag(name = "Sizes", description = "Управление размерами")
public class SizeController {

    private final SizeRepository repo;
    private final SizeService service;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Получить все размеры")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список размеров")})
    @GetMapping
    public ResponseEntity<List<Size>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @Operation(summary = "Создать размер")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Размер создан"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PostMapping
    public ResponseEntity<Size> create(
            @CookieValue(value = "session_token", required = false) String token, @RequestBody Size size) {
        authorizationService.checkAdmin(token);

        Size createdSize = service.create(size);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdSize);
    }

    @Operation(summary = "Обновить размер")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Размер обновлён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Размер не найден"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PutMapping
    public ResponseEntity<Void> update(
            @CookieValue(value = "session_token", required = false) String token,
            @RequestParam("id") int id,
            @RequestBody Size size) {
        authorizationService.checkAdmin(token);
        size.setId(id);
        int updated = repo.update(size);
        if (updated == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить размер по id")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Размер удалён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Размер не найден"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
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
