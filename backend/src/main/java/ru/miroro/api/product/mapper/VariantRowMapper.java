package ru.miroro.api.product.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.miroro.api.product.model.Variant;

@Component
public class VariantRowMapper implements RowMapper<Variant> {

    @Override
    public Variant mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Variant.builder()
                .variantId(rs.getInt("variant_id"))
                .sizeId(rs.getInt("size_id"))
                .colorId(rs.getInt("color_id"))
                .quantity(rs.getInt("quantity"))
                .build();
    }
}
