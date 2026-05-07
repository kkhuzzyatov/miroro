package ru.miroro.api.purchase.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.miroro.api.purchase.model.PurchaseItem;

@Component
public class PurchaseItemRowMapper implements RowMapper<PurchaseItem> {

    @Override
    public PurchaseItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PurchaseItem.builder()
                .id(rs.getInt("purchase_item_id"))
                .productName(rs.getString("product_name"))
                .sizeName(rs.getString("size_name"))
                .colorName(rs.getString("color_name"))
                .price(rs.getBigDecimal("price"))
                .build();
    }
}
