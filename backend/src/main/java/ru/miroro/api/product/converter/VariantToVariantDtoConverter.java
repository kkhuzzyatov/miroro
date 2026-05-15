package ru.miroro.api.product.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.miroro.api.product.dto.VariantDto;
import ru.miroro.api.product.model.Variant;

@Component
public class VariantToVariantDtoConverter implements Converter<Variant, VariantDto> {

    @Override
    public VariantDto convert(Variant variant) {

        if (variant == null) {
            return null;
        }

        return VariantDto.builder()
                .variantId(variant.getVariantId())
                .sizeId(variant.getSizeId())
                .colorId(variant.getColorId())
                .quantity(variant.getQuantity())
                .build();
    }
}
