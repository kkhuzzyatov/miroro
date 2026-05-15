package ru.miroro.api.purchase.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.miroro.api.purchase.dto.PurchaseItemResponseDto;
import ru.miroro.api.purchase.entity.PurchaseItemEntity;

@Component
public class PurchaseItemEntityToPurchaseItemResponseDtoConverter
        implements Converter<PurchaseItemEntity, PurchaseItemResponseDto> {

    @Override
    public PurchaseItemResponseDto convert(PurchaseItemEntity entity) {

        if (entity == null) {
            return null;
        }

        return new PurchaseItemResponseDto(
                entity.getId(),
                entity.getProductItem().getVariant().getProduct().getName(),
                entity.getProductItem().getVariant().getSize().getName(),
                entity.getProductItem().getVariant().getColor().getName(),
                entity.getPrice());
    }
}
