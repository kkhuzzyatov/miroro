package ru.miroro.api.product_item.controller;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.miroro.api.product_item.dto.CreateProductItemRequest;
import ru.miroro.api.product_item.service.ProductItemService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product-items")
@Tag(name = "Product Items", description = "Управление экземплярами товаров")
public class ProductItemController {

    private final ProductItemService service;

    @GetMapping
    public ResponseEntity<?> get(@RequestParam(value = "id", required = false) Integer id) {

        if (id == null) {
            return ResponseEntity.ok(service.findAll());
        }

        return service.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@RequestBody CreateProductItemRequest request) {
        service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}/sold")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> markSold(@PathVariable int id) {
        service.markAsSold(id);
        return ResponseEntity.ok().build();
    }
}
