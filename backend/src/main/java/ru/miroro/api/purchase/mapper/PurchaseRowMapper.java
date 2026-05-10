package ru.miroro.api.purchase.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.miroro.api.purchase.model.Purchase;

@Component
public class PurchaseRowMapper implements RowMapper<Purchase> {

    @Override
    public Purchase mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Purchase.builder()
                .purchaseId(rs.getInt("purchase_id"))
                .userUsername(rs.getString("username"))
                .status(rs.getString("status"))
                .targetAddress(rs.getString("target_address"))
                .build();
    }
}
