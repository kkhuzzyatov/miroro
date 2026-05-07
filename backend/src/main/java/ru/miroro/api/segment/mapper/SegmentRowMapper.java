package ru.miroro.api.segment.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.miroro.api.segment.model.Segment;

@Component("segmentRowMapper")
public class SegmentRowMapper implements RowMapper<Segment> {
    @Override
    public Segment mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Segment.builder()
                .id(rs.getInt("segment_id"))
                .name(rs.getString("name"))
                .build();
    }
}
