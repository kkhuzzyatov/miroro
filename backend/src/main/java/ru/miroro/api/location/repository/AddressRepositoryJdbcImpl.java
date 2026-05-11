package ru.miroro.api.location.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.miroro.api.location.entity.Address;

@RequiredArgsConstructor
@Repository
public class AddressRepositoryJdbcImpl implements AddressRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Address> rowMapper =
            (rs, size) -> new Address(rs.getInt("address_id"), rs.getString("address"), rs.getString("city_uuid"));

    @Override
    public Address findByName(String name) {
        String sql = """
                select address_id, address, city_uuid
                from address
                where address = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, name).stream().findFirst().orElse(null);
    }

    @Override
    public int save(String name, String cityUuid) {
        String sql = """
            insert into address (address, city_uuid)
            values (?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, name);
                    ps.setString(2, cityUuid);
                    return ps;
                },
                keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys == null || !keys.containsKey("address_id")) {
            throw new RuntimeException("message: Не удалось получить сгенерированный идентификатор");
        }

        Number key = (Number) keys.get("address_id");
        return key.intValue();
    }
}
