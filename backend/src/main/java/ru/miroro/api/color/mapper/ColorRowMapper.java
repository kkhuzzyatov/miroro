package ru.miroro.api.color.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.miroro.api.color.model.Color;

@Component("colorRowMapper")
public class ColorRowMapper implements RowMapper<Color> {

    @Override
    public Color mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Color.builder()
                .id(rs.getInt("color_id"))
                .name(rs.getString("name"))
                .hex(rs.getString("hex"))
                .build();
    }
}
