package ru.miroro.api.segment.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miroro.api.segment.mapper.SegmentRowMapper;
import ru.miroro.api.segment.model.Segment;

@Repository
public class SegmentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SegmentRowMapper rowMapper;

    public SegmentRepository(JdbcTemplate jdbcTemplate, @Qualifier("segmentRowMapper") SegmentRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    public List<Segment> findAll() {
        return jdbcTemplate.query("select * from segment", rowMapper);
    }

    public Segment findById(int segmentId) {
        return jdbcTemplate.queryForObject("select * from segment where segment_id = ?", rowMapper, segmentId);
    }

    public Segment findByName(String name) {
        return jdbcTemplate.queryForObject("select * from segment where name = ?", rowMapper, name);
    }

    public void save(Segment segment) {
        jdbcTemplate.update("insert into segment (name) values (?)", segment.getName());
    }

    public int update(Segment segment) {
        return jdbcTemplate.update(
                "update segment set name = ? where segment_id = ?",
                segment.getName(),
                segment.getId() // если поле переименовано — заменить на getSegmentId()
                );
    }

    public int deleteById(int segmentId) {
        return jdbcTemplate.update("delete from segment where segment_id = ?", segmentId);
    }

    public int deleteByName(String name) {
        return jdbcTemplate.update("delete from segment where name = ?", name);
    }

    public Integer findIdByName(String name) {
        return jdbcTemplate.queryForObject("select segment_id from segment where name = ?", Integer.class, name);
    }
}
