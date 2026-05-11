package ru.miroro.api.size.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miroro.api.size.mapper.SizeRowMapper;
import ru.miroro.api.size.model.Size;

@RequiredArgsConstructor
@Repository
public class SizeRepositoryJdbcImpl implements SizeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SizeRowMapper rowMapper;

    @Override
    public List<Size> findAll() {
        return jdbcTemplate.query("select * from size", rowMapper);
    }

    @Override
    public Size findById(int sizeId) {
        return jdbcTemplate.queryForObject("select * from size where size_id = ?", rowMapper, sizeId);
    }

    @Override
    public Size findByName(String name) {
        return jdbcTemplate.queryForObject("select * from size where name = ?", rowMapper, name);
    }

    @Override
    public void save(Size size) {
        jdbcTemplate.update("insert into size (name) values (?)", size.getName());
    }

    @Override
    public int update(Size size) {
        return jdbcTemplate.update("update size set name = ? where size_id = ?", size.getName(), size.getId());
    }

    @Override
    public int deleteById(int sizeId) {
        return jdbcTemplate.update("delete from size where size_id = ?", sizeId);
    }

    @Override
    public int deleteByName(String name) {
        return jdbcTemplate.update("delete from size where name = ?", name);
    }

    @Override
    public Integer findIdByName(String name) {
        return jdbcTemplate.queryForObject("select size_id from size where name = ?", Integer.class, name);
    }
}
