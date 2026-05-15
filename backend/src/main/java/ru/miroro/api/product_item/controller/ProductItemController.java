package ru.miroro.api.product_item.controller;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.miroro.api.product_item.dto.CreateProductItemRequest;
import ru.miroro.api.product_item.service.ProductItemService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product-items")
@Tag(name = "Product Items", description = "Управление экземплярами товаров")
public class ProductItemController {

    private final ProductItemService service;

    @GetMapping
    public ResponseEntity<?> get() {

        log.atInfo().addKeyValue("endpoint", "GET /api/product-items").log("Получение всех товарных экземпляров");

        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@RequestBody CreateProductItemRequest request) {

        log.atInfo()
                .addKeyValue("endpoint", "POST /api/product-items")
                .addKeyValue("variantId", request.getVariantId())
                .log("Создание товарного экземпляра");

        service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}/sold")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> markSold(@PathVariable int id) {

        log.atInfo()
                .addKeyValue("endpoint", "PATCH /api/product-items/{id}/sold")
                .addKeyValue("productItemId", id)
                .log("Пометка товара как проданного");

        service.markAsSold(id);

        return ResponseEntity.ok().build();
    }
}
