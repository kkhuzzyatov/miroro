package ru.miroro.api.product.converter;

import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.miroro.api.product.dto.ProductDto;
import ru.miroro.api.product.model.Product;

@Component
public class ProductToProductDtoConverter implements Converter<Product, ProductDto> {

    private final VariantToVariantDtoConverter variantConverter;
    private final ImageToImageDtoConverter imageConverter;

    public ProductToProductDtoConverter(
            VariantToVariantDtoConverter variantConverter, ImageToImageDtoConverter imageConverter) {
        this.variantConverter = variantConverter;
        this.imageConverter = imageConverter;
    }

    @Override
    public ProductDto convert(Product product) {

        if (product == null) {
            return null;
        }

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .segmentId(product.getSegmentId())
                .variants(
                        product.getVariants() == null
                                ? List.of()
                                : product.getVariants().stream()
                                        .map(variantConverter::convert)
                                        .toList())
                .images(
                        product.getImages() == null
                                ? List.of()
                                : product.getImages().stream()
                                        .map(imageConverter::convert)
                                        .toList())
                .build();
    }
}
