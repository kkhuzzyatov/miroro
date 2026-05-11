package ru.miroro.api.purchase.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miroro.api.purchase.dto.CreatePurchaseRequest;
import ru.miroro.api.purchase.dto.PurchaseVariantRequest;
import ru.miroro.api.purchase.model.Purchase;
import ru.miroro.api.purchase.repository.PurchaseRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class PurchaseService {

    private final PurchaseRepository repository;

    // =========================================================
    // READ
    // =========================================================

    @Transactional(readOnly = true)
    public List<Purchase> findAll() {
        List<Purchase> purchases = repository.findAll();

        for (Purchase purchase : purchases) {
            purchase.setPurchaseItems(repository.findItemsByPurchaseId(purchase.getPurchaseId()));
        }

        return purchases;
    }

    @Transactional(readOnly = true)
    public List<Purchase> findByUserId(int userId) {
        List<Purchase> purchases = repository.findByUserId(userId);

        for (Purchase purchase : purchases) {
            purchase.setPurchaseItems(repository.findItemsByPurchaseId(purchase.getPurchaseId()));
        }

        return purchases;
    }

    // =========================================================
    // CREATE PURCHASE
    // =========================================================

    public void create(CreatePurchaseRequest request, int userId) {

        int statusId = repository.getStatusIdByName("ожидание передачи в пункт отправки");

        int purchaseId = repository.createPurchase(userId, statusId, request.getAddressId());

        repository.addStatusHistory(purchaseId, statusId);

        List<Integer> reservedIds = new ArrayList<>();

        // --- резервируем товары ---
        for (PurchaseVariantRequest item : request.getItems()) {

            var reserved = repository.reserveProductItems(item.getVariantId(), item.getQuantity());

            if (reserved.size() < item.getQuantity()) {
                throw new IllegalStateException("message: Недостаточно товара variant_id=" + item.getVariantId());
            }

            // создаём purchase_item
            for (var pi : reserved) {

                repository.addPurchaseItem(purchaseId, pi.productItemId(), pi.price());

                reservedIds.add(pi.productItemId());
            }
        }

        // финально помечаем проданными
        repository.markItemsSold(reservedIds);
    }

    // =========================================================
    // STATUS
    // =========================================================

    public void changeStatus(int purchaseId, String newStatus) {

        int newStatusId = repository.getStatusIdByName(newStatus);
        int oldStatusId = repository.getCurrentStatusId(purchaseId);

        repository.updateStatus(purchaseId, newStatusId);
        repository.addStatusHistory(purchaseId, oldStatusId);
    }
}
