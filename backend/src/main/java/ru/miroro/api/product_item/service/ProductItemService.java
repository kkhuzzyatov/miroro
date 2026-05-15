package ru.miroro.api.product_item.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miroro.api.product_item.dto.CreateProductItemRequest;
import ru.miroro.api.product_item.dto.ProductItemResponse;
import ru.miroro.api.product_item.model.ProductItem;
import ru.miroro.api.product_item.repository.ProductItemProjection;
import ru.miroro.api.product_item.repository.ProductItemRepository;

@RequiredArgsConstructor
@Service
public class ProductItemService {

    private final ProductItemRepository repository;
    private final ConversionService conversionService;

    public List<ProductItemResponse> findAll() {

        return repository.findAllDetailed().stream()
                .map(this::convertToResponse)
                .toList();
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

    private ProductItemResponse convertToResponse(ProductItemProjection projection) {

        return conversionService.convert(projection, ProductItemResponse.class);
    }
}
