package ru.miroro.api.product.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.miroro.api.product.dto.ProductDto;
import ru.miroro.api.product.model.Product;

@Component
public class ProductDtoToProductConverter implements Converter<ProductDto, Product> {

    @Override
    public Product convert(ProductDto dto) {

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
