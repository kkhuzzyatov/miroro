package ru.miroro.api.product_item.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miroro.api.product_item.dto.CreateProductItemRequest;
import ru.miroro.api.product_item.dto.ProductItemResponse;
import ru.miroro.api.product_item.mapper.ProductItemResponseMapper;

@Repository
@RequiredArgsConstructor
public class ProductItemRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProductItemResponseMapper rowMapper;

    // ============================
    // FIND ALL
    // ============================
    public List<ProductItemResponse> findAll() {
        String sql = """
                SELECT pi.product_item_id,
                       p.name product_name,
                       s.name size_name,
                       c.name color_name,
                       c.hex,
                       pi.is_sold
                  FROM product_item pi
                  JOIN variant v ON pi.variant_id = v.variant_id
                  JOIN product p on v.product_id = p.product_id
                  join size s on v.size_id = s.size_id
                  join color c on v.color_id = c.color_id
                  ORDER BY pi.product_item_id
                """;

        return jdbcTemplate.query(sql, rowMapper);
    }

    // ============================
    // FIND BY ID
    // ============================
    public Optional<ProductItemResponse> findById(int id) {
        String sql = """
                SELECT pi.product_item_id,
                       p.name product_name,
                       s.name size_name,
                       c.name color_name,
                       c.hex,
                       pi.is_sold
                  FROM product_item pi
                  JOIN variant v ON pi.variant_id = v.variant_id
                  JOIN product p on v.product_id = p.product_id
                  join size s on v.size_id = s.size_id
                  join color c on v.color_id = c.color_id
                 WHERE pi.product_item_id = ?
                """;

        List<ProductItemResponse> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // ============================
    // SAVE
    // ============================
    public int save(CreateProductItemRequest createProductItemRequest) {
        return jdbcTemplate.update("""
                INSERT INTO product_item (variant_id, is_sold)
                VALUES (?, ?)
                """, createProductItemRequest.getVariantId(), false);
    }

    // ============================
    // MARK AS SOLD
    // ============================
    public int markAsSold(int productItemId) {
        return jdbcTemplate.update("UPDATE product_item SET is_sold = true WHERE product_item_id = ?", productItemId);
    }
}
