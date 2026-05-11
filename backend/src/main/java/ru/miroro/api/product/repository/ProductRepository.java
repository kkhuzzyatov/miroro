package ru.miroro.api.product.repository;

import java.util.*;
import ru.miroro.api.product.model.Image;
import ru.miroro.api.product.model.Product;
import ru.miroro.api.product.model.Variant;

public interface ProductRepository {

    // =====================================================
    // PRODUCT
    // =====================================================

    List<Product> findAll();

    Product getProductById(Integer productId);

    Product addProduct(Product product);

    int updateProduct(int id, Product product);

    int deleteById(int id);

    // =====================================================
    // VARIANT
    // =====================================================

    void addVariant(Integer productId, Variant variant);

    void removeVariant(Integer productId, Variant variant);

    void deleteVariantsByProductId(int productId);

    // =====================================================
    // IMAGE
    // =====================================================

    void addImage(Integer productId, Image image);

    void deleteImagesByProductId(int productId);
}
