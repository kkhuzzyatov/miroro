package ru.miroro.api.size.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miroro.api.size.mapper.SizeRowMapper;
import ru.miroro.api.size.model.Size;

@RequiredArgsConstructor
@Repository
public class SizeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SizeRowMapper rowMapper;

    public List<Size> findAll() {
        return jdbcTemplate.query("select * from size", rowMapper);
    }

    public Size findById(int sizeId) {
        return jdbcTemplate.queryForObject("select * from size where size_id = ?", rowMapper, sizeId);
    }

    public Size findByName(String name) {
        return jdbcTemplate.queryForObject("select * from size where name = ?", rowMapper, name);
    }

    public void save(Size size) {
        jdbcTemplate.update("insert into size (name) values (?)", size.getName());
    }

    public int update(Size size) {
        return jdbcTemplate.update("update size set name = ? where size_id = ?", size.getName(), size.getId());
    }

    public int deleteById(int sizeId) {
        return jdbcTemplate.update("delete from size where size_id = ?", sizeId);
    }

    public int deleteByName(String name) {
        return jdbcTemplate.update("delete from size where name = ?", name);
    }

    public Integer findIdByName(String name) {
        return jdbcTemplate.queryForObject("select size_id from size where name = ?", Integer.class, name);
    }
}
