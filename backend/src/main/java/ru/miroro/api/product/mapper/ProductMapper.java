package ru.miroro.api.product.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import ru.miroro.api.product.dto.ImageDto;
import ru.miroro.api.product.dto.ProductDto;
import ru.miroro.api.product.dto.VariantDto;
import ru.miroro.api.product.model.Image;
import ru.miroro.api.product.model.Product;
import ru.miroro.api.product.model.Variant;

@Component
public class ProductMapper {

    // =====================================================
    // ENTITY -> DTO
    // =====================================================

    public ProductDto toDto(Product product) {

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
                                        .map(this::toDto)
                                        .toList())
                .images(
                        product.getImages() == null
                                ? List.of()
                                : product.getImages().stream().map(this::toDto).toList())
                .build();
    }

    private VariantDto toDto(Variant variant) {

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

    private ImageDto toDto(Image image) {

        if (image == null) {
            return null;
        }

        return ImageDto.builder()
                .imageId(image.getImageId())
                .colorId(image.getColorId())
                .path(image.getPath())
                .isMain(image.getIsMain())
                .build();
    }

    // =====================================================
    // DTO -> ENTITY
    // =====================================================

    public Product toEntity(ProductDto dto) {

        if (dto == null) {
            return null;
        }

        Product product = new Product();

        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setSegmentId(dto.getSegmentId());

        return product;
    }
}
