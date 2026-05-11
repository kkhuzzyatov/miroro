package ru.miroro.api.purchase.repository;

import java.math.BigDecimal;
import java.util.List;
import ru.miroro.api.purchase.model.Purchase;
import ru.miroro.api.purchase.model.PurchaseItem;

public interface PurchaseRepository {

    // =========================================================
    // READ
    // =========================================================

    List<Purchase> findAll();

    public List<Purchase> findByUserId(int userId);

    public List<PurchaseItem> findItemsByPurchaseId(int purchaseId);

    // =========================================================
    // PURCHASE
    // =========================================================

    public int createPurchase(int userId, int statusId, int addressId);

    public void addPurchaseItem(int purchaseId, int productItemId, BigDecimal price);

    public void addStatusHistory(int purchaseId, int previousStatusId);

    // =========================================================
    // STATUS
    // =========================================================

    public int getStatusIdByName(String name);

    public int getCurrentStatusId(int purchaseId);

    public void updateStatus(int purchaseId, int newStatusId);

    // =========================================================
    // INVENTORY (ВАЖНО)
    // =========================================================

    /**
     * Резервирует свободные product_item.
     * FOR UPDATE SKIP LOCKED предотвращает двойную продажу.
     */
    public List<ProductItemWithPrice> reserveProductItems(int variantId, int quantity);

    public void markItemsSold(List<Integer> ids);
}
