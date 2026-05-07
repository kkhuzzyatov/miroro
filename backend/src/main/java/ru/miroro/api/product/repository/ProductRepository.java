package ru.miroro.api.product.repository;

import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miroro.api.product.mapper.ImageRowMapper;
import ru.miroro.api.product.mapper.ProductRowMapper;
import ru.miroro.api.product.mapper.VariantRowMapper;
import ru.miroro.api.product.model.Image;
import ru.miroro.api.product.model.Product;
import ru.miroro.api.product.model.Variant;

@RequiredArgsConstructor
@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProductRowMapper productRowMapper;
    private final VariantRowMapper variantRowMapper;
    private final ImageRowMapper imageRowMapper;

    // =====================================================
    // PRODUCT
    // =====================================================

    public List<Product> findAll() {

        String sql = "SELECT * FROM product ORDER BY product_id";
        List<Product> products = jdbcTemplate.query(sql, productRowMapper);

        if (products.isEmpty()) {
            return products;
        }

        // собрать id продуктов
        List<Integer> ids = products.stream().map(Product::getId).toList();

        Map<Integer, List<Variant>> variantsMap = loadVariants(ids);
        Map<Integer, List<Image>> imagesMap = loadImages(ids);

        for (Product p : products) {
            p.setVariants(variantsMap.getOrDefault(p.getId(), List.of()));
            p.setImages(imagesMap.getOrDefault(p.getId(), List.of()));
        }

        return products;
    }

    public Product getProductById(Integer productId) {
        try {
            String sqlProduct = "SELECT * FROM product WHERE product_id = ?";
            Product product = jdbcTemplate.queryForObject(sqlProduct, productRowMapper, productId);

            product.setVariants(
                    jdbcTemplate.query("SELECT * FROM variant WHERE product_id = ?", variantRowMapper, productId));

            product.setImages(jdbcTemplate.query(
                    "SELECT * FROM image WHERE product_id = ? ORDER BY is_main DESC", imageRowMapper, productId));

            return product;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Product addProduct(Product product) {

        String sql = """
            INSERT INTO product (name, description, current_price, segment_id)
            VALUES (?, ?, ?, ?)
            RETURNING product_id
            """;

        Integer productId = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                product.getName(),
                product.getDescription(),
                BigDecimal.valueOf(product.getPrice()),
                product.getSegmentId());

        product.setId(productId);
        return product;
    }

    public int updateProduct(int id, Product product) {

        String sql = """
                UPDATE product
                   SET name = ?,
                       description = ?,
                       current_price = ?,
                       segment_id = ?
                 WHERE product_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                product.getName(),
                product.getDescription(),
                BigDecimal.valueOf(product.getPrice()),
                product.getSegmentId(),
                id);
    }

    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM product WHERE product_id = ?", id);
    }

    // =====================================================
    // VARIANT
    // =====================================================

    public void addVariant(Integer productId, Variant variant) {

        String sql = """
            INSERT INTO variant (product_id, size_id, color_id, quantity)
            VALUES (?, ?, ?, ?)
            RETURNING variant_id
            """;

        Integer variantId = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                productId,
                variant.getSizeId(),
                variant.getColorId(),
                variant.getQuantity() == null ? 0 : variant.getQuantity());

        variant.setVariantId(variantId);
    }

    public void removeVariant(Integer productId, Variant variant) {

        String sql = """
                DELETE FROM variant
                 WHERE product_id = ?
                   AND size_id = ?
                   AND color_id = ?
                """;

        jdbcTemplate.update(sql, productId, variant.getSizeId(), variant.getColorId());
    }

    public void deleteVariantsByProductId(int productId) {
        jdbcTemplate.update("DELETE FROM variant WHERE product_id = ?", productId);
    }

    // =====================================================
    // IMAGE
    // =====================================================

    public void addImage(Integer productId, Image image) {

        String sql = """
                INSERT INTO image (product_id, path, is_main, color_id)
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql, productId, image.getPath(), image.getIsMain(), image.getColorId());
    }

    public void deleteImagesByProductId(int productId) {
        jdbcTemplate.update("DELETE FROM image WHERE product_id = ?", productId);
    }

    // =====================================================
    // INTERNAL BULK LOAD (ANTI N+1)
    // =====================================================

    private Map<Integer, List<Variant>> loadVariants(List<Integer> productIds) {

        String inSql = String.join(",", Collections.nCopies(productIds.size(), "?"));

        String sql = "SELECT * FROM variant WHERE product_id IN (" + inSql + ")";

        Map<Integer, List<Variant>> map = new HashMap<>();

        jdbcTemplate.query(
                sql,
                rs -> {
                    Variant v = variantRowMapper.mapRow(rs, rs.getRow());
                    int productId = rs.getInt("product_id");

                    map.computeIfAbsent(productId, k -> new ArrayList<>()).add(v);
                },
                productIds.toArray());

        return map;
    }

    private Map<Integer, List<Image>> loadImages(List<Integer> productIds) {

        String inSql = String.join(",", Collections.nCopies(productIds.size(), "?"));

        String sql = "SELECT * FROM image WHERE product_id IN (" + inSql + ")";

        Map<Integer, List<Image>> map = new HashMap<>();

        jdbcTemplate.query(
                sql,
                rs -> {
                    Image img = imageRowMapper.mapRow(rs, rs.getRow());
                    int productId = rs.getInt("product_id");

                    map.computeIfAbsent(productId, k -> new ArrayList<>()).add(img);
                },
                productIds.toArray());

        return map;
    }
}
