package ru.miroro.api.product_item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.miroro.api.product_item.dto.CreateProductItemRequest;
import ru.miroro.api.product_item.repository.ProductItemRepository;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product-items")
@Tag(name = "Product Items", description = "Управление экземплярами товаров")
public class ProductItemController {

    private final ProductItemRepository repo;

    @Operation(summary = "Получить все товары или один товар по id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список товаров или один товар"),
        @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @GetMapping
    public ResponseEntity<?> getProductItems(@RequestParam(value = "id", required = false) Integer id) {

        if (id == null) {
            return ResponseEntity.ok(repo.findAll());
        }

        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Создать экземпляр товара")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Экземпляр товара создан"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@RequestBody CreateProductItemRequest request) {

        repo.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
