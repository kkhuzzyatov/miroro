package ru.miroro.api.segment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.miroro.api.segment.model.Segment;
import ru.miroro.api.segment.service.SegmentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/segments")
@Tag(name = "Segments", description = "Управление сегментами")
public class SegmentController {

    private final SegmentService service;

    @Operation(summary = "Получить все сегменты")
    @GetMapping
    public ResponseEntity<List<Segment>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Создать сегмент")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Segment> create(@RequestBody Segment segment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(segment));
    }

    @Operation(summary = "Обновить сегмент")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Segment> update(@PathVariable int id, @RequestBody Segment segment) {

        return ResponseEntity.ok(service.update(id, segment));
    }

    @Operation(summary = "Удалить сегмент по id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
