package ru.miroro.api.purchase_status.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miroro.api.purchase_status.model.PurchaseStatus;

@Repository
@RequiredArgsConstructor
public class PurchaseStatusRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<PurchaseStatus> findAll() {
        return jdbcTemplate.query(
                "select purchase_status_id, name from purchase_status", (rs, rowNum) -> PurchaseStatus.builder()
                        .id(rs.getInt("purchase_status_id"))
                        .name(rs.getString("name"))
                        .build());
    }
}
