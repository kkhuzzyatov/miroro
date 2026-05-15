package ru.miroro.api.product_item.converter;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.stereotype.Component;
import ru.miroro.api.product_item.dto.ProductItemResponse;

@Component
public class ResultSetToProductItemResponseConverter {

    public ProductItemResponse convert(ResultSet rs) throws SQLException {
        return ProductItemResponse.builder()
                .productItemId(rs.getInt("product_item_id"))
                .productName(rs.getString("product_name"))
                .sizeName(rs.getString("size_name"))
                .colorName(rs.getString("color_name"))
                .colorHex(rs.getString("hex"))
                .isSold(rs.getBoolean("is_sold"))
                .build();
    }
}
