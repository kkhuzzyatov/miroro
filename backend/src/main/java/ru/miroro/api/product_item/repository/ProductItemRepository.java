package ru.miroro.api.product_item.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import ru.miroro.api.product_item.model.ProductItem;

public interface ProductItemRepository extends JpaRepository<ProductItem, Integer> {

    @Query(value = """
        SELECT
            pi.product_item_id AS productItemId,
            p.name AS productName,
            s.name AS sizeName,
            c.name AS colorName,
            c.hex AS colorHex,
            pi.is_sold AS isSold
        FROM product_item pi
        JOIN variant v ON pi.variant_id = v.variant_id
        JOIN product p ON v.product_id = p.product_id
        JOIN size s ON v.size_id = s.size_id
        JOIN color c ON v.color_id = c.color_id
        ORDER BY pi.product_item_id
    """, nativeQuery = true)
    List<ProductItemProjection> findAllDetailed();

    @Query(value = """
        SELECT
            pi.product_item_id AS productItemId,
            p.name AS productName,
            s.name AS sizeName,
            c.name AS colorName,
            c.hex AS colorHex,
            pi.is_sold AS isSold
        FROM product_item pi
        JOIN variant v ON pi.variant_id = v.variant_id
        JOIN product p ON v.product_id = p.product_id
        JOIN size s ON v.size_id = s.size_id
        JOIN color c ON v.color_id = c.color_id
        WHERE pi.product_item_id = :id
    """, nativeQuery = true)
    Optional<ProductItemProjection> findDetailedById(@Param("id") int id);
}
