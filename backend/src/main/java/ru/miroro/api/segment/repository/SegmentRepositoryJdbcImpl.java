package ru.miroro.api.segment.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miroro.api.segment.mapper.SegmentRowMapper;
import ru.miroro.api.segment.model.Segment;

@RequiredArgsConstructor
@Repository
public class SegmentRepositoryJdbcImpl implements SegmentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SegmentRowMapper rowMapper;

    @Override
    public List<Segment> findAll() {
        return jdbcTemplate.query("select * from segment", rowMapper);
    }

    @Override
    public Segment findById(int segmentId) {
        return jdbcTemplate.queryForObject("select * from segment where segment_id = ?", rowMapper, segmentId);
    }

    @Override
    public Segment findByName(String name) {
        return jdbcTemplate.queryForObject("select * from segment where name = ?", rowMapper, name);
    }

    @Override
    public void save(Segment segment) {
        jdbcTemplate.update("insert into segment (name) values (?)", segment.getName());
    }

    @Override
    public int update(Segment segment) {
        return jdbcTemplate.update(
                "update segment set name = ? where segment_id = ?",
                segment.getName(),
                segment.getId() // если поле переименовано — заменить на getSegmentId()
                );
    }

    @Override
    public int deleteById(int segmentId) {
        return jdbcTemplate.update("delete from segment where segment_id = ?", segmentId);
    }

    @Override
    public int deleteByName(String name) {
        return jdbcTemplate.update("delete from segment where name = ?", name);
    }

    @Override
    public Integer findIdByName(String name) {
        return jdbcTemplate.queryForObject("select segment_id from segment where name = ?", Integer.class, name);
    }
}
