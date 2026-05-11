package ru.miroro.api.color.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miroro.api.color.mapper.ColorRowMapper;
import ru.miroro.api.color.model.Color;

@RequiredArgsConstructor
@Repository
public class ColorRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ColorRowMapper rowMapper;

    public List<Color> findAll() {
        return jdbcTemplate.query("select * from color", rowMapper);
    }

    public Color findById(int id) {
        return jdbcTemplate.queryForObject("select * from color where color_id = ?", rowMapper, id);
    }

    public Color findByName(String name) {
        return jdbcTemplate.queryForObject("select * from color where name = ?", rowMapper, name);
    }

    public void save(Color color) {
        jdbcTemplate.update("insert into color (name, hex) values (?, ?)", color.getName(), color.getHex());
    }

    public int update(Color color) {
        return jdbcTemplate.update(
                "update color set name = ?, hex = ? where color_id = ?",
                color.getName(),
                color.getHex(),
                color.getId());
    }

    public int deleteById(int id) {
        return jdbcTemplate.update("delete from color where color_id = ?", id);
    }

    public int deleteByName(String name) {
        return jdbcTemplate.update("delete from color where name = ?", name);
    }

    public Integer findIdByName(String name) {
        return jdbcTemplate.queryForObject("select color_id from color where name = ?", Integer.class, name);
    }
}
