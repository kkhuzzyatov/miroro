package ru.miroro.api.purchase_status.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.miroro.api.purchase_status.model.PurchaseStatus;
import ru.miroro.api.purchase_status.repository.PurchaseStatusRepository;

@RestController
@RequestMapping("/api/purchase-statuses")
@RequiredArgsConstructor
@Tag(name = "Purchase Statuses", description = "Статусы покупок")
public class PurchaseStatusController {

    private final PurchaseStatusRepository repo;

    @Operation(summary = "Получить все статусы покупок")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список статусов покупок")})
    @GetMapping
    public ResponseEntity<List<PurchaseStatus>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }
}
