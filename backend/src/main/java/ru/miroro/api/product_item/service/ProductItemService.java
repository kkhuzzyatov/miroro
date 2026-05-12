package ru.miroro.api.product_item.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miroro.api.product_item.dto.CreateProductItemRequest;
import ru.miroro.api.product_item.model.ProductItem;
import ru.miroro.api.product_item.repository.ProductItemProjection;
import ru.miroro.api.product_item.repository.ProductItemRepository;

@RequiredArgsConstructor
@Service
public class ProductItemService {

    private final ProductItemRepository repository;

    public List<ProductItemProjection> findAll() {
        return repository.findAllDetailed();
    }

    public Optional<ProductItemProjection> findById(int id) {
        return repository.findDetailedById(id);
    }

    public void create(CreateProductItemRequest request) {
        ProductItem item = ProductItem.builder()
                .variantId(request.getVariantId())
                .isSold(false)
                .build();

        repository.save(item);
    }

    @Transactional
    public void markAsSold(int id) {
        repository.markAsSold(id);
    }
}
