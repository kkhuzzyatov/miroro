package ru.miroro.api.size.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.miroro.api.size.model.Size;

@Component("sizeRowMapper")
public class SizeRowMapper implements RowMapper<Size> {

    @Override
    public Size mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Size.builder()
                .id(rs.getInt("size_id"))
                .name(rs.getString("name"))
                .build();
    }
}
