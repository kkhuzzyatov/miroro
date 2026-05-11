package ru.miroro.api.product_item.repository;

import java.util.List;
import java.util.Optional;
import ru.miroro.api.product_item.dto.CreateProductItemRequest;
import ru.miroro.api.product_item.dto.ProductItemResponse;

public interface ProductItemRepository {

    // ============================
    // FIND ALL
    // ============================
    List<ProductItemResponse> findAll();

    // ============================
    // FIND BY ID
    // ============================
    Optional<ProductItemResponse> findById(int id);

    // ============================
    // SAVE
    // ============================
    int save(CreateProductItemRequest createProductItemRequest);

    // ============================
    // MARK AS SOLD
    // ============================
    int markAsSold(int productItemId);
}
