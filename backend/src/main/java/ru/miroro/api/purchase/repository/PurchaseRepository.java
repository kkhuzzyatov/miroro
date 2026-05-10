package ru.miroro.api.purchase.repository;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miroro.api.purchase.mapper.PurchaseItemRowMapper;
import ru.miroro.api.purchase.mapper.PurchaseRowMapper;
import ru.miroro.api.purchase.model.Purchase;
import ru.miroro.api.purchase.model.PurchaseItem;

@Repository
@RequiredArgsConstructor
public class PurchaseRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PurchaseRowMapper purchaseRowMapper;
    private final PurchaseItemRowMapper purchaseItemRowMapper;

    // =========================================================
    // READ
    // =========================================================

    public List<Purchase> findAll() {
        String sql = """
                SELECT p.purchase_id,
                       u.username,
                       ps.name AS status,
                       a.address AS target_address
                  FROM purchase p
                  JOIN users u ON u.user_id = p.user_id
                  JOIN purchase_status ps ON ps.purchase_status_id = p.status_id
                  JOIN address a ON a.address_id = p.target_address_id
                """;

        return jdbcTemplate.query(sql, purchaseRowMapper);
    }

    public List<Purchase> findByUserId(int userId) {
        String sql = """
                SELECT p.purchase_id,
                       u.username,
                       ps.name AS status,
                       a.address AS target_address
                  FROM purchase p
                  JOIN users u ON u.user_id = p.user_id
                  JOIN purchase_status ps ON ps.purchase_status_id = p.status_id
                  JOIN address a ON a.address_id = p.target_address_id
                 WHERE u.user_id = ?
                """;

        return jdbcTemplate.query(sql, purchaseRowMapper, userId);
    }

    public List<PurchaseItem> findItemsByPurchaseId(int purchaseId) {
        String sql = """
                SELECT
                    pi.purchase_item_id,
                    p.name AS product_name,
                    s.name AS size_name,
                    c.name AS color_name,
                    pi.price
                  FROM purchase_item pi
                  JOIN product_item pri
                    ON pri.product_item_id = pi.product_item_id
                  JOIN variant v
                    ON v.variant_id = pri.variant_id
                  JOIN product p
                    ON p.product_id = v.product_id
                  JOIN size s
                    ON s.size_id = v.size_id
                  JOIN color c
                    ON c.color_id = v.color_id
                 WHERE pi.purchase_id = ?
                """;

        return jdbcTemplate.query(sql, purchaseItemRowMapper, purchaseId);
    }

    // =========================================================
    // PURCHASE
    // =========================================================

    public int createPurchase(int userId, int statusId, int addressId) {
        String sql = """
                INSERT INTO purchase (user_id, status_id, target_address_id)
                VALUES (?, ?, ?)
                RETURNING purchase_id
                """;

        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, userId, statusId, addressId);

        if (id == null) {
            throw new IllegalStateException("Purchase id not generated");
        }

        return id;
    }

    public void addPurchaseItem(int purchaseId, int productItemId, BigDecimal price) {

        String sql = """
                INSERT INTO purchase_item (purchase_id, product_item_id, price)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(sql, purchaseId, productItemId, price);
    }

    public void addStatusHistory(int purchaseId, int previousStatusId) {
        String sql = """
                INSERT INTO purchase_status_history
                (purchase_id, previous_status_id, changed_at)
                VALUES (?, ?, now())
                """;

        jdbcTemplate.update(sql, purchaseId, previousStatusId);
    }

    // =========================================================
    // STATUS
    // =========================================================

    public int getStatusIdByName(String name) {
        String sql = "SELECT purchase_status_id FROM purchase_status WHERE name = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, name);
    }

    public int getCurrentStatusId(int purchaseId) {
        String sql = "SELECT status_id FROM purchase WHERE purchase_id = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, purchaseId);
    }

    public void updateStatus(int purchaseId, int newStatusId) {
        String sql = """
                UPDATE purchase
                   SET status_id = ?
                 WHERE purchase_id = ?
                """;

        jdbcTemplate.update(sql, newStatusId, purchaseId);
    }

    // =========================================================
    // INVENTORY (ВАЖНО)
    // =========================================================

    /**
     * Резервирует свободные product_item.
     * FOR UPDATE SKIP LOCKED предотвращает двойную продажу.
     */
    public List<ProductItemWithPrice> reserveProductItems(int variantId, int quantity) {

        String sql = """
            SELECT pi.product_item_id,
                   p.current_price
            FROM product_item pi
            JOIN variant v ON v.variant_id = pi.variant_id
            JOIN product p ON p.product_id = v.product_id
            WHERE pi.variant_id = ?
              AND pi.is_sold = false
            FOR UPDATE SKIP LOCKED
            LIMIT ?
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new ProductItemWithPrice(rs.getInt("product_item_id"), rs.getBigDecimal("current_price")),
                variantId,
                quantity);
    }

    public void markItemsSold(List<Integer> ids) {
        for (Integer id : ids) {
            String sql = """
                UPDATE product_item
                   SET is_sold = true
                 WHERE product_item_id = ?
                """;
            jdbcTemplate.update(sql, id);
        }
    }

    // DTO внутри repository (удобно)
    public record ProductItemWithPrice(Integer productItemId, BigDecimal price) {}
}
