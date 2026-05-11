package ru.miroro.api.size.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.miroro.api.size.model.Size;
import ru.miroro.api.size.repository.SizeRepository;
import ru.miroro.api.size.service.SizeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sizes")
@Tag(name = "Sizes", description = "Управление размерами")
public class SizeController {

    private final SizeRepository repo;
    private final SizeService service;

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Size> create(@RequestBody Size size) {

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> update(@RequestParam("id") int id, @RequestBody Size size) {

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@RequestParam("id") int id) {

        int deleted = repo.deleteById(id);

        if (deleted == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.noContent().build();
    }
}
