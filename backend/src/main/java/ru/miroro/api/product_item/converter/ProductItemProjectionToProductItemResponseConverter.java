package ru.miroro.api.product_item.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.miroro.api.product_item.dto.ProductItemResponse;
import ru.miroro.api.product_item.repository.ProductItemProjection;

@Component
public class ProductItemProjectionToProductItemResponseConverter
        implements Converter<ProductItemProjection, ProductItemResponse> {

    @Override
    public ProductItemResponse convert(ProductItemProjection projection) {

        if (projection == null) {
            return null;
        }

        return ProductItemResponse.builder()
                .productItemId(projection.getProductItemId())
                .productName(projection.getProductName())
                .sizeName(projection.getSizeName())
                .colorName(projection.getColorName())
                .colorHex(projection.getColorHex())
                .isSold(projection.getIsSold())
                .build();
    }
}
