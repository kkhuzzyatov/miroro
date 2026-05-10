package ru.miroro.api.product.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ru.miroro.api.product.model.Image;
import ru.miroro.api.product.model.Product;
import ru.miroro.api.product.service.ProductService;
import ru.miroro.common.security.AuthorizationService;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Управление товарами")
public class ProductController {

    private final ProductService service;
    private final ObjectMapper objectMapper;
    private final AuthorizationService authorizationService;

    @Operation(
            summary = "Получить товары",
            description = """
                Если параметр id не указан — возвращает список всех товаров.
                Если параметр id указан — возвращает один товар или 404, если товар не найден.
                Если у товара присутствуют изображения — ответ возвращается в формате multipart.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(value = "id", required = false) Integer id
    ) {
        if (id == null) {
            return ResponseEntity.ok(service.findAll());
        }

        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private void addImages(MultiValueMap<String, Object> body, Product product) throws IOException {
        for (Image image : product.getImages()) {
            try (InputStream is = ProductController.class
                    .getClassLoader()
                    .getResourceAsStream(image.getPath())) {

                if (is == null) {
                    throw new RuntimeException("Файл не найден: " + image.getPath());
                }

                body.add(
                        "images",
                        new ByteArrayResource(is.readAllBytes()) {
                            @Override
                            public String getFilename() {
                                return image.getPath();
                            }
                        }
                );
            }
        }
    }

    @Operation(summary = "Создать товар с изображениями")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Товар создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @CookieValue(value = "session_token", required = false) String token,
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles
    ) throws IOException {
        authorizationService.checkAdmin(token);
        Product product = objectMapper.readValue(productJson, Product.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(product, imageFiles));
    }

    @Operation(summary = "Обновить товар")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PutMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> update(@CookieValue(value = "session_token", required = false) String token,
                                       @RequestParam("id") int id,
                                       @RequestPart("product") String productJson,
                                       @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) throws IOException {
        authorizationService.checkAdmin(token);
        Product product = objectMapper.readValue(productJson, Product.class);
        int updated = service.update(id, product, imageFiles);
        if (updated == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить товар по id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Товар удалён"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Экземпляр не найден"),
            @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteById(@CookieValue(value = "session_token", required = false) String token, @RequestParam("id") int id) {
        authorizationService.checkAdmin(token);
        int deleted = service.deleteById(id);

        if (deleted == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Object> createMultipartResponse(Product product) throws IOException {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        bodyBuilder.part("product", product)
                .contentType(MediaType.APPLICATION_JSON);

        if (product.getImages() != null) {
            for (Image image : product.getImages()) {
                if (image.getPath() != null) {
                    String fileName = image.getPath()
                            .substring(image.getPath().lastIndexOf('/') + 1);

                    Path imagePath = Paths.get("src/main/resources/", image.getPath());

                    if (Files.exists(imagePath)) {
                        byte[] content = Files.readAllBytes(imagePath);
                        bodyBuilder.part("images", content)
                                .filename(fileName)
                                .contentType(MediaType.IMAGE_PNG);
                    }
                }
            }
        }

        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_MIXED)
                .body(bodyBuilder.build());
    }
}
