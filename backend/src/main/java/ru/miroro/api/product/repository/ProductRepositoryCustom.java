package ru.miroro.api.product.repository;

import java.util.List;
import ru.miroro.api.product.model.Product;

public interface ProductRepositoryCustom {

    List<Product> findAllWithDetails();

    Product findByIdWithDetails(Integer id);
}
