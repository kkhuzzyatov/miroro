package ru.miroro.api.purchase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.miroro.api.purchase.dto.CreatePurchaseRequest;
import ru.miroro.api.purchase.model.Purchase;
import ru.miroro.api.purchase.service.PurchaseService;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.repository.UserRepository;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/purchases")
@Tag(name = "Purchases", description = "Управление покупками")
public class PurchaseController {

    private final PurchaseService service;
    private final UserRepository userRepository;

    private Integer resolveUserId(Authentication authentication) {
        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("message: Пользователь не найден"));

        return user.getId();
    }

    @Operation(summary = "Получить все покупки текущего пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список покупок текущего пользователя"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping
    public ResponseEntity<List<Purchase>> getUserPurchases(Authentication authentication) {

        Integer userId = resolveUserId(authentication);

        return ResponseEntity.ok(service.findByUserId(userId));
    }

    @Operation(summary = "Получить все покупки (только для admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список всех покупок"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Purchase>> getAllPurchases() {

        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Создать покупку")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Покупка создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreatePurchaseRequest request, Authentication authentication) {

        Integer userId = resolveUserId(authentication);

        service.create(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Изменить статус покупки")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Статус изменён"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Покупка не найдена"),
        @ApiResponse(responseCode = "409", description = "Конфликт с ограничениями бд")
    })
    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeStatus(@RequestParam("id") int id, @RequestParam("new_status") String newStatus) {

        service.changeStatus(id, newStatus);

        return ResponseEntity.ok().build();
    }
}
