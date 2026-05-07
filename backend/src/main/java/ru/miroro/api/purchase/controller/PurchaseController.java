package ru.miroro.api.purchase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
import ru.miroro.api.session.model.Session;
import ru.miroro.api.session.service.SessionService;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.repository.UserRepository;
import ru.miroro.common.security.AuthorizationService;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Tag(name = "Purchases", description = "Управление покупками")
public class PurchaseController {

    private final PurchaseService service;
    private final AuthorizationService authorizationService;
    private final SessionService sessionService;
    private final UserRepository userRepository;

    private Session getSession(String token) {
        return sessionService.getSessionByToken(token).orElseThrow(() -> new SecurityException("Не авторизован"));
    }

    private Integer resolveUserId(Session session) {
        User user = userRepository
                .findByEmail(session.getEmail())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));
        return user.getId();
    }

    @Operation(summary = "Получить все покупки текущего пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список покупок текущего пользователя"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping
    public ResponseEntity<List<Purchase>> getUserPurchases(
            @CookieValue(value = "session_token", required = false) String token) {
        Session session = getSession(token);
        Integer userId = resolveUserId(session);
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    @Operation(summary = "Получить все покупки (только для admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список всех покупок"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping("/all")
    public ResponseEntity<List<Purchase>> getAllPurchases(
            @CookieValue(value = "session_token", required = false) String token) {
        authorizationService.checkAdmin(token);
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
    public ResponseEntity<Void> create(
            @CookieValue(value = "session_token", required = false) String token,
            @RequestBody CreatePurchaseRequest request) {
        authorizationService.checkAuthorized(token);
        Session session = getSession(token);
        Integer userId = resolveUserId(session);
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
    public ResponseEntity<Void> changeStatus(
            @CookieValue(value = "session_token", required = false) String token,
            @RequestParam("id") int id,
            @RequestParam("new_status") String newStatus) {
        authorizationService.checkAdmin(token);
        service.changeStatus(id, newStatus);
        return ResponseEntity.ok().build();
    }
}
