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

    public ProductDto toDto(Product p) {
        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .segmentId(p.getSegmentId())
                .variants(
                        p.getVariants() == null
                                ? List.of()
                                : p.getVariants().stream().map(this::toDto).toList())
                .images(
                        p.getImages() == null
                                ? List.of()
                                : p.getImages().stream().map(this::toDto).toList())
                .build();
    }

    private VariantDto toDto(Variant v) {
        return VariantDto.builder()
                .variantId(v.getVariantId())
                .sizeId(v.getSizeId())
                .colorId(v.getColorId())
                .quantity(v.getQuantity())
                .build();
    }

    private ImageDto toDto(Image i) {
        return ImageDto.builder()
                .colorId(i.getColorId())
                .path(i.getPath())
                .isMain(i.getIsMain())
                .build();
    }

    // =====================================================
    // DTO -> ENTITY
    // =====================================================

    public Product toEntity(ProductDto dto) {
        if (dto == null) return null;

        Product p = new Product();
        p.setId(dto.getId());
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setSegmentId(dto.getSegmentId());

        return p;
    }
}
