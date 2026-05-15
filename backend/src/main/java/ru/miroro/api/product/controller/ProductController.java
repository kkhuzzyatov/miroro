package ru.miroro.api.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.miroro.api.product.dto.ProductDto;
import ru.miroro.api.product.service.ProductService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Управление товарами")
public class ProductController {

    private final ProductService service;

    @Operation(summary = "Получить товары")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешный ответ"),
        @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @GetMapping
    public ResponseEntity<?> getProducts() {

        log.atInfo()
                .addKeyValue("endpoint", "GET /api/products")
                .addKeyValue("result", "list")
                .log("Получение списка товаров");

        List<ProductDto> products = service.findAll();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Получить товар")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешный ответ"),
        @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Integer id) {

        try {
            ProductDto product = service.findById(id);

            log.atInfo()
                    .addKeyValue("endpoint", "GET /api/products/{id}")
                    .addKeyValue("productId", id)
                    .addKeyValue("result", "found")
                    .log("Получение товара");

            return ResponseEntity.ok(product);

        } catch (Exception e) {

            log.atWarn()
                    .addKeyValue("endpoint", "GET /api/products/{id}")
                    .addKeyValue("productId", id)
                    .addKeyValue("result", "not_found")
                    .log("Товар не найден");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/most-expensive")
    @Operation(summary = "Получить самый дорогой продукт")
    public ResponseEntity<ProductDto> getMostExpensiveProduct() {

        try {
            ProductDto product = service.getMostExpensiveProduct();

            log.atInfo()
                    .addKeyValue("endpoint", "GET /api/products/most-expensive")
                    .addKeyValue("result", "found")
                    .log("Получение самого дорогого товара");

            return ResponseEntity.ok(product);

        } catch (Exception e) {

            log.atWarn()
                    .addKeyValue("endpoint", "GET /api/products/most-expensive")
                    .addKeyValue("result", "not_found")
                    .log("Самый дорогой товар не найден");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Создать товар с изображениями")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Товар создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "409", description = "Конфликт с БД")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> create(
            @RequestPart("product") ProductDto productDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles)
            throws IOException {

        log.atInfo()
                .addKeyValue("endpoint", "POST /api/products")
                .addKeyValue("productName", productDto.getName())
                .addKeyValue("imagesCount", imageFiles == null ? 0 : imageFiles.size())
                .log("Создание товара");

        ProductDto created = service.create(productDto, imageFiles);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Обновить товар")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Товар обновлён"),
        @ApiResponse(responseCode = "404", description = "Товар не найден"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> update(
            @PathVariable int id,
            @RequestPart("product") ProductDto productDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles)
            throws IOException {

        log.atInfo()
                .addKeyValue("endpoint", "PUT /api/products/{id}")
                .addKeyValue("productId", id)
                .addKeyValue("imagesCount", imageFiles == null ? 0 : imageFiles.size())
                .log("Обновление товара");

        int updated = service.update(id, productDto, imageFiles);

        if (updated == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить товар по id")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Товар удалён"),
        @ApiResponse(responseCode = "404", description = "Не найден"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {

        log.atInfo()
                .addKeyValue("endpoint", "DELETE /api/products/{id}")
                .addKeyValue("productId", id)
                .log("Удаление товара");

        int deleted = service.deleteById(id);

        if (deleted == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.noContent().build();
    }
}
