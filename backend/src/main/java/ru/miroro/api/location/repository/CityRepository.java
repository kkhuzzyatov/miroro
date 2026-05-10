package ru.miroro.api.location.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.miroro.api.location.entity.City;

@Repository
@RequiredArgsConstructor
public class CityRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<City> rowMapper = (rs, size) -> new City(rs.getString("city_uuid"), rs.getString("name"));

    public City findByName(String name) {
        String sql = """
                select city_uuid, name
                from city
                where name = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, name).stream().findFirst().orElse(null);
    }

    public void save(City city) {
        String sql = """
                insert into city (city_uuid, name)
                values (?, ?)
                """;

        jdbcTemplate.update(sql, city.getCityUuid(), city.getName());
    }
}
