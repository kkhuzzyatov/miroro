package ru.miroro.api.segment.controller;

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
import ru.miroro.api.segment.model.Segment;
import ru.miroro.api.segment.repository.SegmentRepository;
import ru.miroro.api.segment.service.SegmentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/segments")
@Tag(name = "Segments", description = "Управление сегментами")
public class SegmentController {

    private final SegmentRepository repo;
    private final SegmentService service;

    @Operation(summary = "Получить все сегменты")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список сегментов")})
    @GetMapping
    public ResponseEntity<List<Segment>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @Operation(summary = "Создать сегмент")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Сегмент создан"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Segment> create(@RequestBody Segment segment) {

        Segment createdSegment = service.create(segment);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdSegment);
    }

    @Operation(summary = "Обновить сегмент")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Сегмент обновлён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Сегмент не найден"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> update(@RequestParam("id") int id, @RequestBody Segment segment) {

        segment.setId(id);

        int updated = repo.update(segment);

        if (updated == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить сегмент по id")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Сегмент удалён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Сегмент не найден"),
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
